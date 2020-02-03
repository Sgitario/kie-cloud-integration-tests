package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Production")
public class ProductionTest extends BaseTest implements LoginSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_PROD;
    }

    @DisplayName("Can login using credentials from the secret")
    @Test
    void canLogin() {
        thenCanLoginInKieServerControllerUsingDefaultUser();
        thenCanLoginInKieServerExecutionUsingDefaultUser();
    }
}
