package org.kie.cloud.tests.config;

import org.kie.cloud.tests.context.TestContext;

public interface TestConfig {
	void before(TestContext testContext);

	default void after(TestContext testContext) {

	}
}
