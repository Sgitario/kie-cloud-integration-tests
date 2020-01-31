package org.kie.cloud.tests.clients.openshift;

import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import cz.xtf.core.config.OpenShiftConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("client.openshift")
public class OpenshiftConfiguration {

    private Map<String, OpenshiftProperties> config;

    private String selected;


    @PostConstruct
    public void init() {

        OpenshiftProperties properties = config.get(selected);

        System.setProperty(OpenShiftConfig.OPENSHIFT_URL, properties.getUrl());
        System.setProperty(OpenShiftConfig.OPENSHIFT_MASTER_USERNAME, properties.getMasterUsername());
        System.setProperty(OpenShiftConfig.OPENSHIFT_MASTER_PASSWORD, properties.getMasterPassword());
        System.setProperty(OpenShiftConfig.OPENSHIFT_ADMIN_USERNAME, properties.getAdminUsername());
        System.setProperty(OpenShiftConfig.OPENSHIFT_ADMIN_PASSWORD, properties.getAdminPassword());
        System.setProperty(OpenShiftConfig.OPENSHIFT_VERSION, properties.getVersion());
        System.setProperty(OpenShiftConfig.OPENSHIFT_NAMESPACE, properties.getNamespace());

        Optional.ofNullable(properties.getToken()).ifPresent(val -> System.setProperty(OpenShiftConfig.OPENSHIFT_MASTER_TOKEN, val));
    }
}
