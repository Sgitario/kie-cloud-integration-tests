package org.kie.cloud.tests;

import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.utils.Scenarios;

public class AuthoringWithSingleSignOnIntegrationTest extends SingleSignOnBaseTest {

    @Override
    protected String childScenario() {
        return Scenarios.RHPAM_AUTHORING;
    }

    @Test
    void testSingleSuccessTest() {

    }

}
