package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.ExternalAuthOnlySteps;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring with LDAP")
public class AuthoringWithLDAPIntegrationTest extends LdapBaseTest implements LoginSteps, ExternalAuthOnlySteps, IntegrationSteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_AUTHORING;
    }

    @Tag("login")
    @Tag("external-auth-only")
    @Test
    public void shouldNotCreateInternalUserAndWorkWithUserInLdap() {
        // by default, external auth only is set to false
        thenCanLoginInBusinessCentralAndKieServer();

        whenSetExternalAuthTo(true);
        thenCanLoginInBusinessCentralAndKieServer();

        whenSetExternalAuthTo(false);
        thenCanLoginInBusinessCentralAndKieServer();
    }

    @Tag("integration")
    @Test
    public void shouldKieServerConnectWithBusinessCentral() {
        thenKieServersAreConnectedWithBusinessCentrals();
    }

    private void thenCanLoginInBusinessCentralAndKieServer() {
        thenCanLoginInBusinessCentral(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInBusinessCentral(getLdapUsername(), getLdapPassword());
        thenCanLoginInKieServer(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInKieServer(getLdapUsername(), getLdapPassword());
    }

}
