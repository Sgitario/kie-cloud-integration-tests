package org.kie.cloud.tests.rhpam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.LdapBaseTest;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring with LDAP")
public class AuthoringWithLDAPIT extends LdapBaseTest implements LoginSteps, IntegrationSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_AUTHORING;
    }

    @Tag("login")
    @Test
    public void shouldCanLogin() {
        thenCanLoginInBusinessCentral();
        thenCanLoginInKieServer();
    }

    @Tag("integration")
    @Test
    public void shouldKieServerConnectWithBusinessCentral() {
        thenKieServersAreConnectedWithBusinessCentrals();
    }

}
