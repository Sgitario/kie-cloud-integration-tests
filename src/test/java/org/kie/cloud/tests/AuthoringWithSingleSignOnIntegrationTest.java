package org.kie.cloud.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.clients.sso.SsoClient;
import org.kie.cloud.tests.utils.Deployments;
import org.kie.cloud.tests.utils.Templates;


@Slf4j
public class AuthoringWithSingleSignOnIntegrationTest extends BaseTest {

    private static final String AUTH_URL_PROPERTY = "SSO_URL";
    private static final String REALM_PROPERTY = "SSO_REALM";
    private static final String USERNAME_PROPERTY = "SSO_USERNAME";
    private static final String CLIENT_NAME_PROPERTY = "BUSINESS_CENTRAL_SSO_CLIENT";
    private static final String[] ROLES = new String[]{"admin", "kie-server", "rest-all"};

    @Test
    void testSingleSuccessTest() {
        givenSingleSignOnDeployed();
        givenUsersRegisteredIn();
        whenDeployAuthoringRhPam();
    }

    private void givenSingleSignOnDeployed() {
        whenLoadTemplate(Templates.SSO);
    }

    private void givenUsersRegisteredIn() {
        String authUrl = ssoAuthUrl();
        String realm = getSsoDeploymentParam(REALM_PROPERTY);
        log.info("Creating roles and users in SSO at URL {} in Realm {}", authUrl, realm);
        SsoClient sso = SsoClient.get(authUrl, realm);
        sso.createClient(getSsoDeploymentParam(CLIENT_NAME_PROPERTY));
        Stream.of(ROLES).forEach(sso::createRole);
        sso.addRolesToUser(getSsoDeploymentParam(USERNAME_PROPERTY), ROLES);
    }

    private void whenDeployAuthoringRhPam() {
        Map<String, String> extraParams = new HashMap<>();
        extraParams.put(AUTH_URL_PROPERTY, ssoAuthUrl());
        extraParams.put(REALM_PROPERTY, getSsoDeploymentParam(REALM_PROPERTY));
        extraParams.put("BUSINESS_CENTRAL_SSO_CLIENT", "business-central-client");
        extraParams.put("BUSINESS_CENTRAL_SSO_SECRET", "business-central-secret");
        extraParams.put("KIE_SERVER_SSO_CLIENT", "kie-server-client");
        extraParams.put("KIE_SERVER_SSO_SECRET", "kie-server-secret");
        extraParams.put("SSO_USERNAME", getSsoDeploymentParam("SSO_SERVICE_USERNAME"));
        extraParams.put("SSO_PASSWORD", getSsoDeploymentParam("SSO_SERVICE_PASSWORD"));
        whenLoadTemplate(Templates.RHPAM_AUTHORING, extraParams);
    }

    private String ssoAuthUrl() {
        return String.format("https://secure-sso-%s.project.openshiftdomain/auth", testContext.getProject().getName());
    }

    private String getSsoDeploymentParam(String paramName) {
        return getDeploymentParam(Deployments.SSO, paramName);
    }
}
