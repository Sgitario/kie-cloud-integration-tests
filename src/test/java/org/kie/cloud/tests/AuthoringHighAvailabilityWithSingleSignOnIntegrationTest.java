package org.kie.cloud.tests;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring High Availability with Single Sign On")
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
