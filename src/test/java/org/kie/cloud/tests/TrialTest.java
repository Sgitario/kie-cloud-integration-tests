package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Trial")
public class TrialTest extends BaseTest implements LoginSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_TRIAL;
    }

	@Test
    void canLogin() {
        thenCanLoginInKieServerControllerUsingDefaultUser();
        thenCanLoginInKieServerExecutionUsingDefaultUser();
	}
}
