/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.cloud.tests.config.operators.mappers;

import java.util.Map;

import org.kie.cloud.tests.LdapBaseTest;
import org.kie.cloud.tests.config.operators.Auth;
import org.kie.cloud.tests.config.operators.KieApp;
import org.kie.cloud.tests.config.operators.Ldap;
import org.springframework.stereotype.Component;

@Component
public class LdapAuthKieAppPopulator implements KieAppPopulator {

    @Override
    public void populate(KieApp app, Map<String, String> extraParams) {
        String url = extraParams.get(LdapBaseTest.AUTH_LDAP_URL);
        if (url == null) {
            return;
        }

        Ldap ldap = new Ldap();
        ldap.setUrl(url);
        ldap.setBaseCtxDN(extraParams.get(LdapBaseTest.AUTH_LDAP_BASE_CTX_DN));
        ldap.setBaseFilter(extraParams.get(LdapBaseTest.AUTH_LDAP_BASE_FILTER));
        ldap.setBindCredential(extraParams.get(LdapBaseTest.AUTH_LDAP_BIND_CREDENTIAL));
        ldap.setBindDN(extraParams.get(LdapBaseTest.AUTH_LDAP_BIND_DN));
        ldap.setDefaultRole(extraParams.get(LdapBaseTest.AUTH_LDAP_DEFAULT_ROLE));
        ldap.setRoleAttributeID(extraParams.get(LdapBaseTest.AUTH_LDAP_ROLE_ATTRIBUTE_ID));
        ldap.setSearchScope(extraParams.get(LdapBaseTest.AUTH_LDAP_SEARCH_SCOPE));
        ldap.setSearchTimeLimit(Integer.parseInt(extraParams.get(LdapBaseTest.AUTH_LDAP_SEARCH_TIME_LIMIT)));
        ldap.setRolesCtxDN(extraParams.get(LdapBaseTest.AUTH_LDAP_ROLES_CTX_DN));
        ldap.setRoleRecursion(Integer.parseInt(extraParams.get(LdapBaseTest.AUTH_LDAP_ROLE_RECURSION)));
        ldap.setRoleFilter(extraParams.get(LdapBaseTest.AUTH_LDAP_ROLE_FILTER));

        if (app.getSpec().getAuth() == null) {
            app.getSpec().setAuth(new Auth());
        }

        app.getSpec().getAuth().setLdap(ldap);
    }
}
