package org.kie.cloud.tests;

import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.utils.Templates;

public class TemplateHighAvailabilityIntegrationTest extends TemplateBaseTest {

	@Test
	void testSingleSuccessTest() {
		givenHighAvailabilityScenario();
		whenCreateDeployment();
	}

	private void givenHighAvailabilityScenario() {
		givenTemplate(Templates.RHPAM_AUTHORING_HA);
	}
}
