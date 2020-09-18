package org.kie.cloud.tests.loader.operator;

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
    private boolean forceCrdUpdate;
    private String useComponentsVersion;
    private Resource crd;
    private Resource serviceAccount;
    private List<Resource> roles;
    private List<Resource> roleBindings;
    private Resource definition;
    private Map<String, OperatorDefinition> definitions;
}
