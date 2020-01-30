package org.kie.cloud.tests.services;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.cloud.tests.context.ExpressionContext;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpELExpressionEvaluator implements ExpressionEvaluator {

    private static final String TEST_CONTEXT = "testContext";

    private final Environment env;

    @Override
    public String resolveValue(String label, String expression, TestContext testContext, Map<String, ?> extraParams) {

        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext(context(testContext, extraParams));
        String value = expression;
        if (true) {
            try {
                value = (String) parser.parseExpression(expression).getValue(context);
                log.trace("Property for template '{}':'{}'", label, value);
            } catch (Exception ex) {
                log.error("Error loading expression '{}' of attr '{}'", value, label, ex);
                fail("Error loading expression " + value);
            }
        }

        return value;
    }

    private ExpressionContext context(TestContext testContext, Map<String, ?> extraParams) {
        Map<String, Object> params = new HashMap<>();
        params.put(TEST_CONTEXT, testContext);
        params.putAll(extraParams);
        return new ExpressionContext(testContext, params, env);
    }
}
