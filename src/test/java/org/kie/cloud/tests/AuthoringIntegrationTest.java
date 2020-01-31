package org.kie.cloud.tests;

import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.utils.Templates;

public class AuthoringIntegrationTest extends BaseTest {

    @Test
    void testSingleSuccessTest() {
        whenLoadTemplate(Templates.RHPAM_AUTHORING);
    }
}
