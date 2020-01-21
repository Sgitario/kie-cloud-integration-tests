package org.kie.cloud.tests.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.cloud.tests.context.ExpressionContext;
import org.kie.cloud.tests.context.TestContext;

public interface ExpressionEvaluator {

	final String TEST_CONTEXT = "testContext";

	default String resolveValue(String label, String expression, TestContext testContext) {
		return resolveValue(label, expression, testContext, Collections.emptyMap());
	}

	default String resolveValue(String label, String expression, TestContext testContext, Map<String, ?> extraParams) {
		Map<String, Object> params = new HashMap<>();
		params.put(TEST_CONTEXT, testContext);
		params.putAll(extraParams);
		return resolveValue(label, expression, new ExpressionContext(testContext, extraParams));
	}

	String resolveValue(String label, String expression, ExpressionContext expressionContext);
}
