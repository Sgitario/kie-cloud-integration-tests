package org.kie.cloud.tests.rhpam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.MySqlBaseTest;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Trial With MYSQL Database")
public class TrialWithExternalDatabaseIT extends MySqlBaseTest implements LoginSteps, IntegrationSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_TRIAL;
    }

    @Test
    @Tag("login")
    @Tag("startup")
    public void canLogin() {
        thenCanLoginInKieServer();
    }
}
