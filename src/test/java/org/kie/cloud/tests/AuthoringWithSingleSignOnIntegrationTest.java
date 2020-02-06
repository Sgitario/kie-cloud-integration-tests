package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring with Single Sign On")
public class AuthoringWithSingleSignOnIntegrationTest extends SingleSignOnBaseTest implements LoginSteps, IntegrationSteps {

    @Override
    protected String childScenario() {
        return Scenarios.RHPAM_AUTHORING;
    }

    @Tag("login")
    @Test
    public void canLogin() {
        thenCanLoginInBusinessCentral(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInBusinessCentral(getSsoUsername(), getSsoPassword());

        thenCanLoginInKieServer(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInKieServer(getSsoUsername(), getSsoPassword());
    }

    @Tag("integration")
    @Test
    public void shouldKieServerConnectWithBusinessCentral() {
        thenKieServersAreConnectedWithBusinessCentrals();
    }

}
