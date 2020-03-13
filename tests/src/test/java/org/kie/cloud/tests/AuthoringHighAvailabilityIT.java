package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring High Availability")
public class AuthoringHighAvailabilityIT extends BaseTest implements LoginSteps, IntegrationSteps {

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

    @Tag("integration")
    @Test
    public void shouldKieServerReconnectWhenBusinessCentralIsRestarted() {
        whenBusinessCentralIsRestarted();
        thenKieServersAreConnectedWithBusinessCentrals();
    }

    @Tag("integration")
    @Test
    public void shouldBusinessCentralReconnectWhenKieServerIsRestarted() {
        whenKieServerIsRestarted();
        thenKieServersAreConnectedWithBusinessCentrals();
    }
}
