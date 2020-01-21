package org.kie.cloud.tests.services;

import static org.junit.jupiter.api.Assertions.fail;

import org.kie.cloud.tests.context.ExpressionContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SpELExpressionEvaluator implements ExpressionEvaluator {

	@Override
	public String resolveValue(String label, String expression, ExpressionContext expressionContext) {

		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(expressionContext);
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
}
