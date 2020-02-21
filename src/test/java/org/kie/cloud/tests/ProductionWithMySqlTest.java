package org.kie.cloud.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.annotations.DisabledOnTemplates;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Params;
import org.kie.cloud.tests.utils.Scenarios;

@DisabledOnTemplates
@DisplayName("RHPAM Production with MySQL")
public class ProductionWithMySqlTest extends BaseTest implements LoginSteps, IntegrationSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_PROD;
    }

    @Override
    protected Map<String, String> scenarioExtraParams() {
        Map<String, String> params = new HashMap<>();
        params.putAll(super.scenarioExtraParams());
        params.put(Params.DATABASE_ENGINE, "mysql");
        return params;
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
