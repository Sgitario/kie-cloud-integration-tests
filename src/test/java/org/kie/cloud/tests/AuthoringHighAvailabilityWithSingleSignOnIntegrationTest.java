package org.kie.cloud.tests;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.utils.Scenarios;

public class AuthoringHighAvailabilityWithSingleSignOnIntegrationTest extends SingleSignOnBaseTest {

    @Override
    protected String childScenario() {
        return Scenarios.RHPAM_AUTHORING_HA;
    }

    @Test
    void testSingleSuccessTest() throws IOException {
        // whenDeployAuthoringHighAvailabilityRhPam();
    }



}
