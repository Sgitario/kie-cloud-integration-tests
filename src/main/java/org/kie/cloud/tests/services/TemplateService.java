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
import org.kie.cloud.tests.config.templates.TemplateListConfiguration;
import org.kie.cloud.tests.config.templates.TemplateRequest;
import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.openshift.api.model.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

	private static final String MDC_KEY = "template";
	private static final String PARAM_VALUE_DEFAULT = "1";

	private final TemplateListConfiguration templateListConfiguration;
	private final OpenshiftClient openshift;
	private final ExpressionEvaluator evaluator;
	private final List<PostLoadDeploymentService> postProcessors;

	public void loadTemplate(TemplateRequest request) {
		MDC.put(MDC_KEY, request.getTemplate());
		log.info("Loading template ... ");
		TemplateDefinition definition = loadTemplateDefinition(request.getTemplate());
		preLoad(request, definition);
		List<Deployment> deployments = loadAndProcess(request, definition);
		postLoad(request, deployments);
		log.info("Template loaded OK ");
		MDC.remove(MDC_KEY);
	}

	private void preLoad(TemplateRequest request, TemplateDefinition definition) {
		loadCustomResources(request.getContext(), definition);
	}

	private List<Deployment> loadAndProcess(TemplateRequest request, TemplateDefinition definition) {
		Map<String, String> parameters = prepareParameters(request, definition);
		InputStream content = loadContent(definition, parameters);
		return openshift.loadTemplate(request.getContext().getProject(), content, parameters);
	}

	private void postLoad(TemplateRequest request, List<Deployment> deployments) {
		if (deployments == null) {
			return;
		}

		deployments.forEach(deployment -> postProcessors.forEach(p -> p.process(request.getContext(), deployment)));
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

	private Map<String, String> prepareParameters(TemplateRequest request, TemplateDefinition definition) {
		Map<String, String> parameters = new HashMap<>();
		for (Entry<String, String> entry : definition.getParams().entrySet()) {
			String value = evaluator.resolveValue(entry.getKey(), entry.getValue(), request.getContext());

			parameters.put(entry.getKey(), value);
		}

		parameters.putAll(request.getExtraParams());
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
