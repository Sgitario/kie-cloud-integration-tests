package org.kie.cloud.tests.clients.openshift;

import lombok.Data;

@Data
public class OpenshiftProperties {
    private String url;
    private String username;
    private String password;
    private String version;
    private String namespace;
    private String token;
}
