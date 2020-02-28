package org.kie.cloud.tests.loader.templates;

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
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.openshift.api.model.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.kie.cloud.tests.core.context.Deployment;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.core.services.actions.Action;
import org.kie.cloud.tests.loader.Loader;
import org.kie.cloud.tests.utils.environment.OpenshiftEnvironment;
import org.slf4j.MDC;
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
    private final OpenshiftEnvironment environment;
    private final List<Action> actions;

    @Override
    protected List<Deployment> runLoad(TestContext testContext, String scenario, Map<String, String> extraParams) {

        MDC.put(MDC_KEY, scenario);
        log.info("Loading template ... ");
        TemplateDefinition definition = loadTemplateDefinition(scenario);
        preLoad(testContext, definition);
        List<Deployment> deployments = loadAndProcess(testContext, definition, extraParams);
        postLoad(testContext, definition, deployments);
        log.info("Template loaded OK ");
        MDC.remove(MDC_KEY);
        return deployments;
    }

    private void postLoad(TestContext testContext, TemplateDefinition definition, List<Deployment> deployments) {
        addOutputToDeployments(testContext, definition, deployments);
    }

    private void addOutputToDeployments(TestContext testContext, TemplateDefinition definition, List<Deployment> deployments) {
        if (deployments != null) {
            deployments.forEach(d -> resolveParams(testContext, definition.getOutput(), d.getEnvironmentVariables()));
        }
    }

    private void preLoad(TestContext testContext, TemplateDefinition definition) {
        runPreActions(testContext, definition);
        loadCustomResources(testContext, definition);
    }

    private void runPreActions(TestContext testContext, TemplateDefinition definition) {
        if (definition.getPreActions() != null) {
            definition.getPreActions().forEach(actionDefinition -> {
                Action action = loadAction(actionDefinition.getAction());
                action.run(actionDefinition, testContext);
            });
        }
    }

    private Action loadAction(String action) {
        return actions.stream().filter(a -> StringUtils.equalsAnyIgnoreCase(a.name(), action)).findFirst().orElseGet(() -> {
            fail("Action '" + action + "' not found");
            return null;
        });
    }

    private List<Deployment> loadAndProcess(TestContext testContext, TemplateDefinition definition, Map<String, String> extraParams) {
        Map<String, String> parameters = prepareParameters(testContext, definition, extraParams);
        InputStream content = loadContent(definition, parameters);
        return environment.createTemplate(testContext.getProject(), content, parameters);
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
            String value = findParamFromParameters(paramName, parameters).orElseGet(findParamValueFromContent(paramName, content));
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
        resolveParams(testContext, definition.getParams(), parameters);
        extraParams.forEach(parameters::putIfAbsent);
        parameters.entrySet().forEach(entry -> log.info("Property '{}'= '{}'", entry.getKey(), entry.getValue()));

        return parameters;
    }

    private void resolveParams(TestContext testContext, Map<String, String> in, Map<String, String> out) {
        if (in == null) {
            return;
        }

        for (Entry<String, String> entry : in.entrySet()) {
            String value = evaluator.resolveValue(entry.getKey(), entry.getValue(), testContext);

            out.put(entry.getKey(), value);
        }
    }

    private void loadCustomResources(TestContext context, TemplateDefinition definition) {
        if (definition.getCustomImageStreamsFile() != null) {
            try {
                environment.createResource(context.getProject(), definition.getCustomImageStreamsFile().getInputStream());
            } catch (IOException e) {
                log.error("Could not load custom image stream", e);
                fail("Error loading custom image stream. Cause: " + e.getMessage());
            }
        }

        if (definition.getCustomSecretsFile() != null) {
            try {
                environment.createTemplate(context.getProject(), definition.getCustomSecretsFile().getInputStream(), Collections.emptyMap());
            } catch (IOException e) {
                log.error("Could not load custom image stream", e);
                fail("Error loading custom image stream. Cause: " + e.getMessage());
            }
        }
    }

}
