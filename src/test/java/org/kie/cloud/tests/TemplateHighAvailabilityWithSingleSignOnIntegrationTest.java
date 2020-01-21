package org.kie.cloud.tests;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.clients.sso.SsoClient;
import org.kie.cloud.tests.utils.Templates;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TemplateHighAvailabilityWithSingleSignOnIntegrationTest extends TemplateBaseTest {

	private static final String AUTH_URL_PROPERTY = "SSO_URL";
	private static final String REALM_PROPERTY = "SSO_REALM";
	private static final String USERNAME_PROPERTY = "SSO_USERNAME";
	private static final String CLIENT_NAME_PROPERTY = "BUSINESS_CENTRAL_SSO_CLIENT";
	private static final String[] ROLES = new String[] { "admin", "kie-server", "rest-all" };

	@Test
	void testSingleSuccessTest() throws IOException {
		givenSingleSignOnDeployed();
		givenUsersRegisteredIn();
		whenDeployAuthoringHighAvailabilityRhPam();
	}

	private void givenUsersRegisteredIn() {
		String authUrl = getParamContext(AUTH_URL_PROPERTY);
		String realm = getParamContext(REALM_PROPERTY);
		log.info("Creating roles and users in SSO at URL {} in Realm {}", authUrl, realm);
		SsoClient sso = SsoClient.get(authUrl, realm);
		sso.createClient(getParamContext(CLIENT_NAME_PROPERTY));
		Stream.of(ROLES).forEach(sso::createRole);
		sso.addRolesToUser(getParamContext(USERNAME_PROPERTY), ROLES);
	}

	private void whenDeployAuthoringHighAvailabilityRhPam() {
		givenTemplate(Templates.RHPAM_AUTHORING_HA);
		whenCreateDeployment();
	}

	private void givenSingleSignOnDeployed() {
		givenTemplate(Templates.SSO);
		whenCreateDeployment();
	}
}
