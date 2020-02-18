package org.kie.cloud.tests.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.openshift.api.model.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.MDC;
import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.config.templates.TemplateDefinition;
import org.kie.cloud.tests.config.templates.TemplateListConfiguration;
import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.services.ExpressionEvaluator;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.cloud.tests.utils.OpenshiftExtensionModelUtils.findParam;

@Slf4j
@Component("templates")
@RequiredArgsConstructor
public class TemplateLoader extends Loader {

    private static final String MDC_KEY = "template";
    private static final String PARAM_VALUE_DEFAULT = "1";

    private final TemplateListConfiguration templateListConfiguration;
    private final OpenshiftClient openshift;
    private final ExpressionEvaluator evaluator;

    @Override
    public void load(TestContext testContext, String template, Map<String, String> extraParams) {

        MDC.put(MDC_KEY, template);
        log.info("Loading template ... ");
        TemplateDefinition definition = loadTemplateDefinition(template);
        preLoad(testContext, definition);
        List<Deployment> deployments = loadAndProcess(testContext, definition, extraParams);
        postLoad(testContext, deployments);
        log.info("Template loaded OK ");
        MDC.remove(MDC_KEY);
    }

    private Consumer<Deployment> updateEnvironmentVariable(TestContext testContext, String key, Object value) {
        return deployment -> openshift.updateEnvironmentVariable(testContext.getProject(), deployment, key, "" + value);
    }

    private void preLoad(TestContext testContext, TemplateDefinition definition) {
        loadCustomResources(testContext, definition);
    }

    private List<Deployment> loadAndProcess(TestContext testContext, TemplateDefinition definition, Map<String, String> extraParams) {
        Map<String, String> parameters = prepareParameters(testContext, definition, extraParams);
        InputStream content = loadContent(definition, parameters);
        return openshift.loadTemplate(testContext.getProject(), content, parameters);
    }

    private TemplateDefinition loadTemplateDefinition(String templateName) {
        Optional<TemplateDefinition> template = templateListConfiguration.getTemplate(templateName);
        if (!template.isPresent()) {
            fail("Template " + templateName + " not found!");
        }

        return template.get();
    }

    private InputStream loadContent(TemplateDefinition definition, Map<String, String> parameters) {
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(definition.getFile().getInputStream(), writer, Charset.defaultCharset());
            // Workaround because it's failing when the template has ${{PARAM_ATTR}} values.
            String content = replaceDoubleQuoteParams(writer.toString(), parameters);
            return IOUtils.toInputStream(content, Charset.defaultCharset());
        } catch (IOException ex) {
            log.error("Could not load the template content", ex);
            fail("Error loading template content. Cause: " + ex.getMessage());
        }

        return null;
    }

    private String replaceDoubleQuoteParams(String content, Map<String, String> parameters) {
        Pattern p = Pattern.compile("\\$\\{\\{[a-zA-Z_]+\\}\\}");
        Matcher m = p.matcher(content);
        if (m.find()) {
            String doubleQuoteParam = m.group();
            String paramName = doubleQuoteParam.substring(3, doubleQuoteParam.length() - 2);
            String value = findParamFromParameters(paramName, parameters)
                                                                         .orElseGet(findParamValueFromContent(paramName, content));
            String newContent = m.replaceAll(value);
            return replaceDoubleQuoteParams(newContent, parameters);
        }

        return content;
    }

    private Optional<String> findParamFromParameters(String paramName, Map<String, String> parameters) {
        return Optional.ofNullable(parameters.get(paramName));
    }

    private Supplier<String> findParamValueFromContent(String paramName, String content) {
        return () -> {
            String cutContent = content.substring(0, content.indexOf("objects:"));
            Template template = Serialization.unmarshal(cutContent, Template.class);
            return findParam(template, paramName).orElse(PARAM_VALUE_DEFAULT);
        };
    }

    private Map<String, String> prepareParameters(TestContext testContext, TemplateDefinition definition, Map<String, String> extraParams) {
        Map<String, String> parameters = new HashMap<>();
        for (Entry<String, String> entry : definition.getParams().entrySet()) {
            String value = evaluator.resolveValue(entry.getKey(), entry.getValue(), testContext);

            parameters.put(entry.getKey(), value);
        }

        parameters.putAll(extraParams);
        parameters.entrySet().forEach(entry -> log.info("Property '{}'= '{}'", entry.getKey(), entry.getValue()));

        return parameters;
    }

    private void loadCustomResources(TestContext context, TemplateDefinition definition) {
        if (definition.getCustomImageStreamsFile() != null) {
            try {
                openshift.loadResources(context.getProject(), definition.getCustomImageStreamsFile().getInputStream());
            } catch (IOException e) {
                log.error("Could not load custom image stream", e);
                fail("Error loading custom image stream. Cause: " + e.getMessage());
            }
        }

        if (definition.getCustomSecretsFile() != null) {
            try {
                openshift.loadTemplate(context.getProject(), definition.getCustomSecretsFile().getInputStream(),
                                       Collections.emptyMap());
            } catch (IOException e) {
                log.error("Could not load custom image stream", e);
                fail("Error loading custom image stream. Cause: " + e.getMessage());
            }
        }
    }

}
