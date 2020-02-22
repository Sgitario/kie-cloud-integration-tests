package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.annotations.DisabledOnOperators;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisabledOnOperators
@DisplayName("RHPAM KieServer With MYSQL Database")
public class KieServerWithExternalDatabaseTest extends MySqlBaseTest implements LoginSteps, IntegrationSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_KIESERVER_EXTERNALDB;
    }

    @Test
    @Tag("login")
    @Tag("startup")
    public void canLogin() {
        thenCanLoginInKieServer(defaultUserName(), defaultUserPassword());
    }
}
