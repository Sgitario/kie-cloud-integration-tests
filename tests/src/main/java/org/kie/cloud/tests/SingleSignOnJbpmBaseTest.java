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
import org.junit.jupiter.api.Tag;
import org.kie.cloud.tests.clients.sso.SsoClient;
import org.kie.cloud.tests.utils.Deployments;
import org.kie.cloud.tests.utils.Parties;

import static org.kie.cloud.tests.core.constants.SsoConstants.SSO_BUSINESS_CENTRAL_SSO_CLIENT;
import static org.kie.cloud.tests.core.constants.SsoConstants.SSO_BUSINESS_CENTRAL_SSO_SECRET;
import static org.kie.cloud.tests.core.constants.SsoConstants.SSO_KIE_SERVER_SSO_CLIENT;
import static org.kie.cloud.tests.core.constants.SsoConstants.SSO_KIE_SERVER_SSO_SECRET;
import static org.kie.cloud.tests.core.constants.SsoConstants.SSO_PASSWORD;
import static org.kie.cloud.tests.core.constants.SsoConstants.SSO_REALM;
import static org.kie.cloud.tests.core.constants.SsoConstants.SSO_URL;
import static org.kie.cloud.tests.core.constants.SsoConstants.SSO_USERNAME;

@Slf4j
@Tag("auth-sso")
public abstract class SingleSignOnJbpmBaseTest extends PartyJbpmBaseTest {

    private static final String[] ROLES = new String[]{"admin", "kie-server", "rest-all"};
    private static final String BUSINESS_CENTRAL_CLIENT = "business-central-client";
    private static final String KIE_SERVER_CLIENT = "kie-server-client";

    @Override
    protected Map<String, String> scenarioExtraParams() {
        Map<String, String> extraParams = new HashMap<>();
        extraParams.put(SSO_URL, ssoAuthUrl());
        extraParams.put(SSO_REALM, getSsoDeploymentParam(SSO_REALM));
        extraParams.put(SSO_USERNAME, getUserName());
        extraParams.put(SSO_PASSWORD, getUserPassword());
        extraParams.put(SSO_BUSINESS_CENTRAL_SSO_CLIENT, BUSINESS_CENTRAL_CLIENT);
        extraParams.put(SSO_BUSINESS_CENTRAL_SSO_SECRET, "business-central-secret");
        extraParams.put(SSO_KIE_SERVER_SSO_CLIENT, KIE_SERVER_CLIENT);
        extraParams.put(SSO_KIE_SERVER_SSO_SECRET, "kie-server-secret");
        return extraParams;
    }

    @Override
    protected String getParty() {
        return Parties.SSO;
    }

    @Override
    protected void onAfterDeploymentTemplate() {
        String authUrl = ssoAuthUrl();
        String realm = getSsoDeploymentParam(SSO_REALM);
        log.info("Creating roles and users in SSO at URL {} in Realm {}", authUrl, realm);
        SsoClient sso = SsoClient.get(authUrl, realm);
        sso.createClient(BUSINESS_CENTRAL_CLIENT);
        sso.createClient(KIE_SERVER_CLIENT);
        sso.createUser(getUserName(), getUserPassword());
        Stream.of(ROLES).forEach(sso::createRole);
        sso.addRolesToUser(getUserName(), ROLES);
    }

    private String ssoAuthUrl() {
        return String.format("%s/auth", getTestContext().getDeployment(Deployments.SSO).getHttpUrl(), projectName());
    }

    private String getSsoDeploymentParam(String paramName) {
        return getDeploymentParam(Deployments.SSO, paramName);
    }
}
