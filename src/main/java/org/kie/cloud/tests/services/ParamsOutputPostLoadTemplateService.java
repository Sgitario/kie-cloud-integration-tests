package org.kie.cloud.tests.services;

import java.util.Map.Entry;
import java.util.Optional;

import org.kie.cloud.tests.config.templates.Output;
import org.kie.cloud.tests.config.templates.TemplateDefinition;
import org.kie.cloud.tests.config.templates.TemplateInstance;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParamsOutputPostLoadTemplateService implements PostLoadTemplateService {

	private final ExpressionEvaluator evaluator;

	@Override
	public void process(TemplateInstance templateInstance, TemplateDefinition definition, TestContext testContext) {
		Output output = definition.getOutput();
		if (output == null) {
			return;
		}

		Optional.ofNullable(output.getParams()).ifPresent(params -> {
			for (Entry<String, String> entry : params.entrySet()) {
				String value = evaluator.resolveValue(entry.getKey(), entry.getValue(), testContext,
						templateInstance.getAllDeploymentProperties());

				testContext.addParam(entry.getKey(), value);
			}
		});

	}

}
