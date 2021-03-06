package org.kie.cloud.tests.core.services;

import java.util.Collections;
import java.util.Map;

import org.kie.cloud.tests.core.context.TestContext;

public interface ExpressionEvaluator {

    default String resolveValue(String label, String expression, TestContext testContext) {
        return resolveValue(label, expression, testContext, Collections.emptyMap());
    }

    String resolveValue(String label, String expression, TestContext testContext, Map<String, ?> extraParams);
}
