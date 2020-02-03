package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring with Single Sign On")
public class AuthoringWithSingleSignOnIntegrationTest extends SingleSignOnBaseTest implements LoginSteps {

    @Override
    protected String childScenario() {
        return Scenarios.RHPAM_AUTHORING;
    }

    @Test
    @DisplayName("Can login using SSO users")
    void canLogin() {
        thenCanLoginInKieServerControllerUsing(getSsoUsername(), getSsoPassword());
    }


}
