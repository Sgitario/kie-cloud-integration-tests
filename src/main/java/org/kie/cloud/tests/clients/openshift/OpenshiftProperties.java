package org.kie.cloud.tests.clients.openshift;

import lombok.Data;
import org.kie.cloud.tests.properties.ServiceProperties;

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
    private ServiceProperties ldap;
}
