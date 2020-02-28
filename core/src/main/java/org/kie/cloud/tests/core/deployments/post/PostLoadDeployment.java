package org.kie.cloud.tests.core.deployments.post;

import org.kie.cloud.tests.core.context.Deployment;
import org.kie.cloud.tests.core.context.TestContext;

public interface PostLoadDeployment {
	void process(TestContext testContext, Deployment deployment);
}
