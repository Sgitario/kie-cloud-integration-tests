package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring High Availability with Single Sign On")
public class AuthoringHighAvailabilityWithSingleSignOnIntegrationTest extends SingleSignOnBaseTest implements LoginSteps {

    @Override
    protected String childScenario() {
        return Scenarios.RHPAM_AUTHORING_HA;
    }

    @Tag("login")
    @Test
    void canLogin() {
        thenCanLoginInBusinessCentral(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInBusinessCentral(getSsoUsername(), getSsoPassword());

        thenCanLoginInKieServer(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInKieServer(getSsoUsername(), getSsoPassword());
    }

}
