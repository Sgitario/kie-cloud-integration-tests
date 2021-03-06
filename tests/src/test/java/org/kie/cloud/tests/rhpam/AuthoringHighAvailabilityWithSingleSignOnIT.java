package org.kie.cloud.tests.rhpam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.SingleSignOnJbpmBaseTest;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring High Availability with Single Sign On")
public class AuthoringHighAvailabilityWithSingleSignOnIT extends SingleSignOnJbpmBaseTest implements LoginSteps, IntegrationSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_AUTHORING_HA;
    }

    @Tag("login")
    @Test
    public void canLogin() {
        thenCanLoginInBusinessCentral();
        thenCanLoginInKieServer();
    }

    @Tag("integration")
    @Test
    public void shouldKieServerConnectWithBusinessCentral() {
        thenKieServersAreConnectedWithBusinessCentrals();
    }

}
