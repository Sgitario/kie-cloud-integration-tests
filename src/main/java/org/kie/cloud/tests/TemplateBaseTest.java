package org.kie.cloud.tests;

import org.kie.cloud.tests.services.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TemplateBaseTest extends BaseTest {

	@Autowired
	private TemplateService templateService;

	private String selectedTemplate;

	protected void givenTemplate(String name) {
		selectedTemplate = name;
	}

	protected void whenCreateDeployment() {
		templateService.loadTemplate(testContext, selectedTemplate);
	}
}
