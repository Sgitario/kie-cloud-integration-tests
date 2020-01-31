package org.kie.cloud.tests.clients.openshift;

import lombok.Data;

@Data
public class OpenshiftProperties {
    private String url;
    private String masterUsername;
    private String masterPassword;
    private String adminUsername;
    private String adminPassword;
    private String version;
    private String namespace;
    private String token;
}
