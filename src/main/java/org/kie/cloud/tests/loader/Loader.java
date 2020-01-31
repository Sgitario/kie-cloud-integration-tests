package org.kie.cloud.tests.loader;

import java.util.Map;

import org.kie.cloud.tests.context.TestContext;

public interface Loader {

    void load(TestContext testContext, String template, Map<String, String> extraParams);
}
