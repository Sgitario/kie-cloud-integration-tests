package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring")
public class AuthoringTest extends BaseTest implements LoginSteps, IntegrationSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_AUTHORING;
    }

    @Test
    @Tag("login")
    public void canLogin() {
        thenCanLoginInBusinessCentral(defaultUserName(), defaultUserPassword());
        thenCanLoginInKieServer(defaultUserName(), defaultUserPassword());
    }

    @Tag("integration")
    @Test
    public void shouldKieServerConnectWithBusinessCentral() {
        thenKieServersAreConnectedWithBusinessCentrals(defaultUserName(), defaultUserPassword());
    }
}
