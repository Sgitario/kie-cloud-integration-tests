package org.kie.cloud.tests.clients.sso;

public enum Provider {
	OIDC_JBOSS_XML_SUBSYSTEM("keycloak-oidc-jboss-subsystem", "Keycloak OIDC JBoss Subsystem XML"),
	SAML_JBOSS_XML_SUBSYSTEM("keycloak-saml-subsystem", "Keycloak SAML Wildfly/JBoss Subsystem"),
	OIDC_KEYCLOAK_JSON("keycloak-oidc-keycloak-json", "Keycloak OIDC JSON");

	private final String providerId;
	private final String webUiLabel;

	private Provider(String providerId, String webUiLabel) {
		this.providerId = providerId;
		this.webUiLabel = webUiLabel;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getWebUiLabel() {
		return webUiLabel;
	}
}
