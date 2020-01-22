package org.kie.cloud.tests.config.templates;

import java.util.Map;

import org.kie.cloud.tests.context.TestContext;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemplateRequest {

	private final TestContext context;
	private final String template;
	private final Map<String, String> extraParams;
}
