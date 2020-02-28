package org.kie.cloud.tests.environment.openshift.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.kie.cloud.tests.core.config.TestConfig;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.environment.openshift.OpenshiftEnvironmentImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertNotNull;

@Component
@RequiredArgsConstructor
public class ExampleSecretsTestConfig implements TestConfig {

	@Value("${test.common.config.secrets.file}")
	private Resource secretsFile;

	@Value("${test.project.secret:my-secret}")
	private String secret;

    private final OpenshiftEnvironmentImpl openshift;

	@Override
    public void before(TestContext testContext) {

		try {
			assertNotNull("Project not created yet!", testContext.getProject());
			Map<String, String> parameters = new HashMap<>();
			parameters.put("SECRET_NAME", secret);
			testContext.setSecret(secret);
            openshift.createTemplate(testContext.getProject(), secretsFile.getInputStream(), parameters);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
