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
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.kie.cloud.tests.clients.sso.SsoClient;
import org.kie.cloud.tests.utils.Deployments;
import org.kie.cloud.tests.utils.Scenarios;

@Slf4j
public abstract class SingleSignOnBaseTest extends BaseTest {

    private static final String AUTH_URL_PROPERTY = "SSO_URL";
    private static final String REALM_PROPERTY = "SSO_REALM";
    private static final String USERNAME_PROPERTY = "SSO_USERNAME";
    private static final String CLIENT_NAME_PROPERTY = "BUSINESS_CENTRAL_SSO_CLIENT";
    private static final String[] ROLES = new String[]{"admin", "kie-server", "rest-all"};

    protected abstract String childScenario();

    protected Map<String, String> childExtraParams() {
        Map<String, String> extraParams = new HashMap<>();
        extraParams.put(AUTH_URL_PROPERTY, ssoAuthUrl());
        extraParams.put(REALM_PROPERTY, getSsoDeploymentParam(REALM_PROPERTY));
        extraParams.put("BUSINESS_CENTRAL_SSO_CLIENT", "business-central-client");
        extraParams.put("BUSINESS_CENTRAL_SSO_SECRET", "business-central-secret");
        extraParams.put("KIE_SERVER_SSO_CLIENT", "kie-server-client");
        extraParams.put("KIE_SERVER_SSO_SECRET", "kie-server-secret");
        extraParams.put("SSO_USERNAME", getSsoDeploymentParam("SSO_SERVICE_USERNAME"));
        extraParams.put("SSO_PASSWORD", getSsoDeploymentParam("SSO_SERVICE_PASSWORD"));
        return extraParams;
    }

    @Override
    protected String scenario() {
        return Scenarios.SSO;
    }

    @Override
    protected void afterOnLoadScenario() {
        createUsersAndRolesInSingleSignOn();
        deployNextScenario();
    }

    private void deployNextScenario() {
        whenLoadTemplate(childScenario(), childExtraParams());
    }

    private void createUsersAndRolesInSingleSignOn() {
        String authUrl = ssoAuthUrl();
        String realm = getSsoDeploymentParam(REALM_PROPERTY);
        log.info("Creating roles and users in SSO at URL {} in Realm {}", authUrl, realm);
        SsoClient sso = SsoClient.get(authUrl, realm);
        sso.createClient(getSsoDeploymentParam(CLIENT_NAME_PROPERTY));
        Stream.of(ROLES).forEach(sso::createRole);
        sso.addRolesToUser(getSsoDeploymentParam(USERNAME_PROPERTY), ROLES);
    }

    private String ssoAuthUrl() {
        return String.format("https://secure-sso-%s.project.openshiftdomain/auth", projectName());
    }

    private String getSsoDeploymentParam(String paramName) {
        return getDeploymentParam(Deployments.SSO, paramName);
    }
}
