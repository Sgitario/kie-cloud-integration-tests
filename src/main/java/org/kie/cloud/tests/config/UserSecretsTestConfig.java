package org.kie.cloud.tests.config;

import java.util.HashMap;
import java.util.Map;

import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertNotNull;

@Component
public class UserSecretsTestConfig implements TestConfig {

    private static final String KIE_ADMIN_USER = "KIE_ADMIN_USER";
    private static final String KIE_ADMIN_PWD = "KIE_ADMIN_PWD";

    @Value("${test.credentials.secret}")
    private String secretName;

    @Value("${test.credentials.user}")
    private String userName;

    @Value("${test.credentials.password}")
    private String userPassword;

    @Autowired
    private OpenshiftClient openshift;

    @Override
    public void before(TestContext testContext) {

        assertNotNull("Project not created yet!", testContext.getProject());
        Map<String, String> data = new HashMap<>();
        data.put(KIE_ADMIN_USER, userName);
        data.put(KIE_ADMIN_PWD, userPassword);
        openshift.createSecret(testContext.getProject(), secretName, data);
    }

}
