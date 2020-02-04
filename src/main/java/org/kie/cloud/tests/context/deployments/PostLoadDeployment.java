package org.kie.cloud.tests.context.deployments;

import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;

public interface PostLoadDeployment {
	void process(TestContext testContext, Deployment deployment);
}
