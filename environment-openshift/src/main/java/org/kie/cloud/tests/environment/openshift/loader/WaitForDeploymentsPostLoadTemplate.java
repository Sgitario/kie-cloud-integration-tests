package org.kie.cloud.tests.environment.openshift.loader;

import lombok.RequiredArgsConstructor;
import org.kie.cloud.tests.core.context.Deployment;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.core.deployments.post.PostLoadDeployment;
import org.kie.cloud.tests.environment.openshift.OpenshiftEnvironmentImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitForDeploymentsPostLoadTemplate implements PostLoadDeployment {

    private final OpenshiftEnvironmentImpl openshift;

	@Override
	public void process(TestContext testContext, Deployment deployment) {
        openshift.waitForDeployment(testContext.getProject(), deployment.getName());
	}

}
