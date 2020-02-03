package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring")
public class AuthoringIntegrationTest extends BaseTest implements LoginSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_AUTHORING;
    }

    @Test
    @DisplayName("Can login using credentials from the secret")
    void canLogin() {
        thenCanLoginInKieServerControllerUsingDefaultUser();
        thenCanLoginInKieServerExecutionUsingDefaultUser();
    }
}
