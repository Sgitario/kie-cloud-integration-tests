package org.kie.cloud.tests.rhpam;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.JbpmBaseTest;
import org.kie.cloud.tests.loader.templates.DisabledOnTemplates;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Params;
import org.kie.cloud.tests.utils.Scenarios;

@DisabledOnTemplates
@DisplayName("RHPAM Production with MySQL")
public class ProductionWithMySqlIT extends JbpmBaseTest implements LoginSteps, IntegrationSteps {

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
        thenCanLoginInBusinessCentral();
        thenCanLoginInKieServer();
    }

    @Tag("integration")
    @Test
    public void shouldKieServerConnectWithBusinessCentral() {
        thenKieServersAreConnectedWithBusinessCentrals();
    }
}
