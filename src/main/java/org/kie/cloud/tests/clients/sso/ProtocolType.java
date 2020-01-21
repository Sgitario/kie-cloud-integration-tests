package org.kie.cloud.tests.clients.sso;

public enum ProtocolType {
	OPENID_CONNECT("openid-connect"), SAML("saml");

	private String label;

	private ProtocolType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
