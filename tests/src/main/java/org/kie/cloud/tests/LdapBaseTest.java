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
package org.kie.cloud.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.kie.cloud.tests.properties.LdapProperties;
import org.kie.cloud.tests.utils.Parties;
import org.springframework.beans.factory.annotation.Autowired;

import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_BASE_CTX_DN;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_BASE_FILTER;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_BIND_CREDENTIAL;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_BIND_DN;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_DEFAULT_ROLE;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_ROLES_CTX_DN;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_ROLE_ATTRIBUTE_ID;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_ROLE_FILTER;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_ROLE_RECURSION;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_SEARCH_SCOPE;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_SEARCH_TIME_LIMIT;
import static org.kie.cloud.tests.core.constants.LdapConstants.AUTH_LDAP_URL;

@Tag("auth-ldap")
public abstract class LdapBaseTest extends PartyBaseTest {

    @Autowired
    private LdapProperties ldapProperties;

    @Override
    protected Map<String, String> scenarioExtraParams() {
        Map<String, String> extraParams = new HashMap<>();
        extraParams.putAll(super.scenarioExtraParams());
        extraParams.put(AUTH_LDAP_URL, ldapAuthUrl());
        extraParams.put(AUTH_LDAP_BIND_DN, ldapProperties.getBindDn());
        extraParams.put(AUTH_LDAP_BIND_CREDENTIAL, ldapProperties.getBindCredential());
        extraParams.put(AUTH_LDAP_BASE_CTX_DN, ldapProperties.getBaseCtxDn());
        extraParams.put(AUTH_LDAP_BASE_FILTER, ldapProperties.getBaseFilter());
        extraParams.put(AUTH_LDAP_SEARCH_SCOPE, ldapProperties.getSearchScope());
        extraParams.put(AUTH_LDAP_SEARCH_TIME_LIMIT, ldapProperties.getSearchTimeLimit());
        extraParams.put(AUTH_LDAP_DEFAULT_ROLE, ldapProperties.getDefaultRole());
        extraParams.put(AUTH_LDAP_ROLE_ATTRIBUTE_ID, ldapProperties.getRoleAttributeId());
        extraParams.put(AUTH_LDAP_ROLE_FILTER, ldapProperties.getRoleFilter());
        extraParams.put(AUTH_LDAP_ROLES_CTX_DN, ldapProperties.getRolesCtxDn());
        extraParams.put(AUTH_LDAP_ROLE_RECURSION, ldapProperties.getRoleRecursion());
        return extraParams;
    }

    @Override
    protected String getParty() {
        return Parties.LDAP;
    }

    private String ldapAuthUrl() {
        return String.format("ldap://ldap-%s:30389", projectName());
    }

    protected String getLdapUsername() {
        return ldapProperties.getUser();
    }

    protected String getLdapPassword() {
        return ldapProperties.getPassword();
    }
}
