package org.kie.cloud.tests.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("test.credentials")
public class CredentialsProperties {

    private String user;
    private String password;
    private String secret;
}
