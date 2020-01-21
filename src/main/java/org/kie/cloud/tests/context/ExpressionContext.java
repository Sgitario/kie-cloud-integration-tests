package org.kie.cloud.tests.context;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExpressionContext {
	private final TestContext testContext;
	private final Map<String, ?> params;
}
