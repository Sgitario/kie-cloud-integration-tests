package org.kie.cloud.tests.services;

import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddDeploymentPostLoadDeploymentService implements PostLoadDeploymentService {

	@Override
	public void process(TestContext testContext, Deployment deployment) {
		testContext.putDeployment(deployment);

	}

}
