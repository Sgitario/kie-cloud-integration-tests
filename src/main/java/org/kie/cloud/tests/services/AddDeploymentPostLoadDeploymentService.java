package org.kie.cloud.tests.services;

import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddDeploymentPostLoadDeploymentService implements PostLoadDeploymentService {

	@Override
	public void process(TestContext testContext, Deployment deployment) {
		testContext.putDeployment(deployment);
		deployment.getEnvironmentVariables().keySet().stream().sorted().forEach(key -> {
			log.info("Output '{}'= '{}'", key, deployment.getEnvironmentVariable(key));
		});
	}

}
