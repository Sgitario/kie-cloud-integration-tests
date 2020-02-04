package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.steps.ExternalAuthOnlySteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;

@DisplayName("RHPAM Authoring with LDAP")
public class AuthoringWithLDAPIntegrationTest extends LdapBaseTest implements LoginSteps, ExternalAuthOnlySteps {

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_AUTHORING;
    }

    @Tag("login")
    @Test
    void canLogin() {
        thenCanLoginInBusinessCentral(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInBusinessCentral(getLdapUsername(), getLdapPassword());

        thenCanLoginInKieServer(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInKieServer(getLdapUsername(), getLdapPassword());
    }

    @Tag("login")
    @Tag("external-auth-only")
    @Test
    void shouldExcludeInternalUsersWhenExternalAuthIsFalse() {
        whenSetExternalAuthTo(true);

        thenCannotLoginInBusinessCentral(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInBusinessCentral(getLdapUsername(), getLdapPassword());

        thenCannotLoginInKieServer(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInKieServer(getLdapUsername(), getLdapPassword());

        whenSetExternalAuthTo(false);

        thenCanLoginInBusinessCentral(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInBusinessCentral(getLdapUsername(), getLdapPassword());

        thenCanLoginInKieServer(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword());
        thenCanLoginInKieServer(getLdapUsername(), getLdapPassword());
    }

}
