package org.kie.cloud.tests.config;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.properties.CredentialsProperties;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertNotNull;

@Component
@RequiredArgsConstructor
public class UserSecretsTestConfig implements TestConfig {

    private static final String KIE_ADMIN_USER = "KIE_ADMIN_USER";
    private static final String KIE_ADMIN_PWD = "KIE_ADMIN_PWD";

    private final CredentialsProperties credentials;
    private final OpenshiftClient openshift;

    @Override
    public void before(TestContext testContext) {

        assertNotNull("Project not created yet!", testContext.getProject());
        Map<String, String> data = new HashMap<>();
        data.put(KIE_ADMIN_USER, credentials.getUser());
        data.put(KIE_ADMIN_PWD, credentials.getPassword());
        openshift.createSecret(testContext.getProject(), credentials.getSecret(), data);
    }

}
