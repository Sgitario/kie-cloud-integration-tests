package org.kie.cloud.tests.services;

import org.kie.cloud.tests.config.templates.TemplateDefinition;
import org.kie.cloud.tests.config.templates.TemplateInstance;
import org.kie.cloud.tests.context.TestContext;

public interface PostLoadTemplateService {
	void process(TemplateInstance templateInstance, TemplateDefinition definition, TestContext testContext);
}
