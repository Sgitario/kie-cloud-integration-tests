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

@Tag("auth-ldap")
public abstract class LdapBaseTest extends PartyBaseTest {

    public static final String AUTH_LDAP_URL = "AUTH_LDAP_URL";
    public static final String AUTH_LDAP_BIND_DN = "AUTH_LDAP_BIND_DN";
    public static final String AUTH_LDAP_BIND_CREDENTIAL = "AUTH_LDAP_BIND_CREDENTIAL";
    public static final String AUTH_LDAP_BASE_CTX_DN = "AUTH_LDAP_BASE_CTX_DN";
    public static final String AUTH_LDAP_BASE_FILTER = "AUTH_LDAP_BASE_FILTER";
    public static final String AUTH_LDAP_SEARCH_SCOPE = "AUTH_LDAP_SEARCH_SCOPE";
    public static final String AUTH_LDAP_SEARCH_TIME_LIMIT = "AUTH_LDAP_SEARCH_TIME_LIMIT";
    public static final String AUTH_LDAP_DEFAULT_ROLE = "AUTH_LDAP_DEFAULT_ROLE";
    public static final String AUTH_LDAP_ROLE_ATTRIBUTE_ID = "AUTH_LDAP_ROLE_ATTRIBUTE_ID";
    public static final String AUTH_LDAP_ROLE_FILTER = "AUTH_LDAP_ROLE_FILTER";
    public static final String AUTH_LDAP_ROLES_CTX_DN = "AUTH_LDAP_ROLES_CTX_DN";
    public static final String AUTH_LDAP_ROLE_RECURSION = "AUTH_LDAP_ROLE_RECURSION";

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
