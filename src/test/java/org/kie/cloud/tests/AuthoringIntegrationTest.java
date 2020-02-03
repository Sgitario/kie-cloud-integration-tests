package org.kie.cloud.tests;

import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

public class AuthoringIntegrationTest extends BaseTest implements LoginSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_AUTHORING;
    }
}
