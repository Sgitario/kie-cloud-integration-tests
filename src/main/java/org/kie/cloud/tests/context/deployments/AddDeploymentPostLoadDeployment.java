package org.kie.cloud.tests.context.deployments;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddDeploymentPostLoadDeployment implements PostLoadDeployment {

	@Override
	public void process(TestContext testContext, Deployment deployment) {
		testContext.putDeployment(deployment);
		deployment.getEnvironmentVariables().keySet().stream()
		.sorted().forEach(key -> {
		    Optional.ofNullable(deployment.getEnvironmentVariable(key)).ifPresent(v -> log.info("Output '{}'= '{}'", key, v));
		});
	}

}
