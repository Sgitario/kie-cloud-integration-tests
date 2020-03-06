package org.kie.cloud.tests.environment.openshift.config;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.kie.cloud.tests.core.config.TestConfig;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.core.properties.CredentialsProperties;
import org.kie.cloud.tests.environment.openshift.OpenshiftEnvironmentImpl;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertNotNull;

@Component
@RequiredArgsConstructor
public class UserSecretsTestConfig implements TestConfig {

    private static final String KIE_ADMIN_USER = "KIE_ADMIN_USER";
    private static final String KIE_ADMIN_PWD = "KIE_ADMIN_PWD";

    private final CredentialsProperties credentials;
    private final OpenshiftEnvironmentImpl openshift;

    @Override
    public void before(TestContext testContext) {

        assertNotNull("Project not created yet!", testContext.getProject());
        Map<String, String> data = new HashMap<>();
        data.put(KIE_ADMIN_USER, credentials.getUser());
        data.put(KIE_ADMIN_PWD, credentials.getPassword());
        openshift.createRawSecret(testContext.getProject(), credentials.getSecret(), data);
    }

}
