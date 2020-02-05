package org.kie.cloud.tests.config.operators;

import java.util.List;
import java.util.Map;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Data
@Configuration
@ConfigurationProperties("test.loaders.operator")
public class OperatorConfiguration {

    private String kieAppName;
    private Resource crd;
    private Resource serviceAccount;
    private Resource role;
    private Resource roleBinding;
    private Resource definition;
    private Map<String, List<String>> deployments;
}
