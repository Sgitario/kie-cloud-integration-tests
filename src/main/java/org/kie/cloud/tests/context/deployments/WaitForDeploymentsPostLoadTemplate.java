package org.kie.cloud.tests.context.deployments;

import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitForDeploymentsPostLoadTemplate implements PostLoadDeployment {

	private final OpenshiftClient openshift;

	@Override
	public void process(TestContext testContext, Deployment deployment) {
		openshift.waitForDeployment(testContext.getProject(), deployment.getName());
	}

}
