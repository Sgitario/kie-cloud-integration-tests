package org.kie.cloud.tests.utils;

public final class Deployments {
	public static final String SSO = "sso";
    private static final String WORKBENCH = "rhpamcentr";
    private static final String DECISION_MANAGER = "rhdmcentr";
    private static final String KIE_SERVER = "kieserver";

	private Deployments() {

	}

    public static final boolean isBusinessCentral(String name) {
        return name.contains(WORKBENCH) || name.contains(DECISION_MANAGER);
    }

    public static final boolean isKieServer(String name) {
        return name.endsWith(KIE_SERVER);
    }
}
