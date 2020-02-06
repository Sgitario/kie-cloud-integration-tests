package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Production")
public class ProductionTest extends BaseTest implements LoginSteps, IntegrationSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_PROD;
    }

    @Tag("login")
    @Test
    public void canLogin() {
        thenCanLoginInBusinessCentral(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInKieServer(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
    }

    @Tag("integration")
    @Test
    public void shouldKieServerConnectWithBusinessCentral() {
        thenKieServersAreConnectedWithBusinessCentrals();
    }
}
