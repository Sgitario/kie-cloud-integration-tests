package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring High Availability")
public class AuthoringHighAvailabilityIntegrationTest extends BaseTest implements LoginSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_AUTHORING_HA;
    }

    @DisplayName("Can login using credentials from the secret")
    @Test
    void canLogin() {
        thenCanLoginInKieServerControllerUsingDefaultUser();
        thenCanLoginInKieServerExecutionUsingDefaultUser();
    }

}
