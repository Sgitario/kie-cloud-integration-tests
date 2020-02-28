package org.kie.cloud.tests.core.config;

import org.kie.cloud.tests.core.context.TestContext;

public interface TestConfig {

    void before(TestContext testContext);

	default void after(TestContext testContext) {

	}
}
