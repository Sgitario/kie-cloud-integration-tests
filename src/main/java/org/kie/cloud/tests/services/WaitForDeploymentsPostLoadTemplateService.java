package org.kie.cloud.tests.services;

import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.config.templates.TemplateDefinition;
import org.kie.cloud.tests.config.templates.TemplateInstance;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitForDeploymentsPostLoadTemplateService implements PostLoadTemplateService {

	private final OpenshiftClient openshift;

	@Override
	public void process(TemplateInstance templateInstance, TemplateDefinition definition, TestContext testContext) {
		if (templateInstance.getDeployments() == null) {
			return;
		}

		templateInstance.getDeployments().forEach(deployment -> {
			openshift.waitForDeployment(testContext.getProject(), deployment.getName());
		});

	}

}
