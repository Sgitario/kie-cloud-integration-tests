package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring High Availability with Single Sign On")
public class AuthoringHighAvailabilityWithSingleSignOnIT extends SingleSignOnBaseTest implements LoginSteps, IntegrationSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_AUTHORING_HA;
    }

    @Tag("login")
    @Test
    public void canLogin() {
        thenCannotLoginInBusinessCentral(defaultUserName(), defaultUserPassword());
        thenCanLoginInBusinessCentral(getSsoUsername(), getSsoPassword());
        thenCanLoginInKieServer(getSsoUsername(), getSsoPassword());
    }

    @Tag("integration")
    @Test
    public void shouldKieServerConnectWithBusinessCentral() {
        thenKieServersAreConnectedWithBusinessCentrals(getSsoUsername(), getSsoPassword());
    }

}
