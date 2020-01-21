package org.kie.cloud.tests.services;

import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.cloud.tests.utils.OpenshiftExtensionModelUtils.findParam;

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

import org.apache.commons.io.IOUtils;
import org.jboss.logging.MDC;
import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.config.templates.TemplateDefinition;
import org.kie.cloud.tests.config.templates.TemplateInstance;
import org.kie.cloud.tests.config.templates.TemplateListConfiguration;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.openshift.api.model.Template;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TemplateService {

	private static final String MDC_KEY = "template";
	private static final String PARAM_VALUE_DEFAULT = "1";

	@Autowired
	private TemplateListConfiguration templateListConfiguration;

	@Autowired
	private OpenshiftClient openshift;

	@Autowired
	private ExpressionEvaluator evaluator;

	@Autowired
	private List<PostLoadTemplateService> postProcessors;

	public void loadTemplate(TestContext context, String templateName) {
		MDC.put(MDC_KEY, templateName);
		log.info("Loading template ... ");
		TemplateDefinition definition = loadTemplateDefinition(templateName);
		preLoad(context, definition);
		TemplateInstance instance = loadAndProcess(context, definition);
		postLoad(instance, definition, context);
		log.info("Template loaded OK ");
		MDC.remove(MDC_KEY);
	}

	private void preLoad(TestContext context, TemplateDefinition definition) {
		loadCustomResources(context, definition);
	}

	private TemplateInstance loadAndProcess(TestContext context, TemplateDefinition definition) {
		Map<String, String> parameters = prepareParameters(context, definition);
		InputStream content = loadContent(definition, parameters);
		return openshift.loadTemplate(context.getProject(), content, parameters);
	}

	private void postLoad(TemplateInstance templateInstance, TemplateDefinition definition, TestContext testContext) {
		postProcessors.forEach(p -> p.process(templateInstance, definition, testContext));
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
			String value = findParamValueFromContent(paramName, content)
					.orElseGet(findParamFromParameters(paramName, parameters));
			String newContent = m.replaceAll(value);
			return replaceDoubleQuoteParams(newContent, parameters);
		}

		return content;
	}

	private Supplier<String> findParamFromParameters(String paramName, Map<String, String> parameters) {
		return () -> parameters.getOrDefault(paramName, PARAM_VALUE_DEFAULT);
	}

	private Optional<String> findParamValueFromContent(String paramName, String content) {
		String cutContent = content.substring(0, content.indexOf("objects:"));
		Template template = Serialization.unmarshal(cutContent, Template.class);
		return findParam(template, paramName);
	}

	private Map<String, String> prepareParameters(TestContext testContext, TemplateDefinition definition) {
		Map<String, String> parameters = new HashMap<>();
		for (Entry<String, String> entry : definition.getParams().entrySet()) {
			String value = evaluator.resolveValue(entry.getKey(), entry.getValue(), testContext);

			parameters.put(entry.getKey(), value);
		}

		parameters.putAll(testContext.getParams());
		parameters.entrySet()
				.forEach(entry -> log.info("Property for template '{}':'{}'", entry.getKey(), entry.getValue()));

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
