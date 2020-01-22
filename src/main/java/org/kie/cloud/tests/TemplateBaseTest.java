package org.kie.cloud.tests;

import java.util.Collections;
import java.util.Map;

import org.kie.cloud.tests.config.templates.TemplateRequest;
import org.kie.cloud.tests.services.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TemplateBaseTest extends BaseTest {

	@Autowired
	private TemplateService templateService;

	protected void whenLoadTemplate(String name) {
		whenLoadTemplate(name, Collections.emptyMap());
	}

	protected void whenLoadTemplate(String name, Map<String, String> extraParams) {
		templateService.loadTemplate(
				TemplateRequest.builder().context(testContext).template(name).extraParams(extraParams).build());
	}
}
