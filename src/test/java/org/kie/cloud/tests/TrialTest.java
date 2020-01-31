package org.kie.cloud.tests;

import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.utils.Templates;

public class TrialTest extends BaseTest {

	@Test
	void testSingleSuccessTest() {
        whenLoadTemplate(Templates.RHPAM_TRIAL);
	}
}
