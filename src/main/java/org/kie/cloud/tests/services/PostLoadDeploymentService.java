package org.kie.cloud.tests.services;

import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;

public interface PostLoadDeploymentService {
	void process(TestContext testContext, Deployment deployment);
}
