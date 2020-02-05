package org.kie.cloud.tests.clients.sso;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContexts;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

import static org.kie.cloud.tests.utils.AwaitilityUtils.awaitsLong;

@Slf4j
public class SsoClient {

	private final String realmName;
	private final String authUrl;
	private Keycloak client;

	private SsoClient(String authUrl, String realmName) {
		this.realmName = realmName;
		this.authUrl = authUrl;

		initClient();
	}

	public static SsoClient get(String authUrl, String realm) {
		return new SsoClient(authUrl, realm);
	}

	public void createClient(String client) {
		ClientRepresentation clientRepresentation = new ClientRepresentation();
		clientRepresentation.setClientId(client);
		clientRepresentation.setName(client);
		clientRepresentation.setRedirectUris(Arrays.asList("*"));
        clientRepresentation.setDirectAccessGrantsEnabled(true);
		clientRepresentation.setEnabled(true);
		clientRepresentation.setPublicClient(true);

        awaitsLong().until(() -> {
			try {
				realm().clients().create(clientRepresentation);
				return true;
			} catch (Exception ex) {
				log.warn("Error loading the client into SSO. Trying again.");
			}

			return false;
		});

	}

	public void createRole(String rolename) {
		RoleRepresentation role = new RoleRepresentation();
		role.setName(rolename);
		realm().roles().create(role);
	}

	public void addRolesToUser(String userName, String[] rolenames) {
		String userId = findUserByUsername(userName);
		addRealmRolesToUser(userId, Arrays.asList(rolenames));
	}

	private RealmResource realm() {
		return client.realm(realmName);
	}

	/**
	 * Keycloak client is initialized by creating class instance however, if
	 * redirected to another node, it needs to be reinitialized.
	 */
	private void initClient() {
		SSLContext sslContext = null;
		if (authUrl.contains("https")) {
			try {
				sslContext = SSLContexts.custom().loadTrustMaterial(new TrustAllStrategy()).build();
			} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
				log.warn("Failed to create naive sslContext!");
			}
		}

		ResteasyClient client = (ResteasyClient) ResteasyClientBuilder.newBuilder().sslContext(sslContext)
				.hostnameVerifier(new NoopHostnameVerifier()).build();
		this.client = KeycloakBuilder.builder().serverUrl(authUrl).realm("master").username("admin").password("admin")
				.clientId("admin-cli").resteasyClient(client).build();
	}

	private String findUserByUsername(String username) {
		return client.realm(realmName).users().search(username, 0, 1).get(0).getId();
	}

	private void addRealmRolesToUser(String userId, List<String> rolenames) {
		List<RoleRepresentation> roles = client.realm(realmName).users().get(userId).roles().realmLevel()
				.listAvailable().stream().filter(r -> rolenames.contains(r.getName())).collect(Collectors.toList());
		realm().users().get(userId).roles().realmLevel().add(roles);
	}

}