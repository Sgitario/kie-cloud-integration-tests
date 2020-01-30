package org.kie.cloud.tests.context;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

@Getter
@RequiredArgsConstructor
public class ExpressionContext {
    private final TestContext testContext;
    private final Map<String, ?> params;
    private final Environment env;
}
