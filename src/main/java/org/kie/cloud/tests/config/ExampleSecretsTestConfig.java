package org.kie.cloud.tests.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertNotNull;

@Component
public class ExampleSecretsTestConfig implements TestConfig {

	@Value("${test.common.config.secrets.file}")
	private Resource secretsFile;

	@Value("${test.project.secret:my-secret}")
	private String secret;

	@Autowired
	private OpenshiftClient openshift;

	@Override
	public void before(TestContext testContext) {

		try {
			assertNotNull("Project not created yet!", testContext.getProject());
			Map<String, String> parameters = new HashMap<>();
			parameters.put("SECRET_NAME", secret);
			testContext.setSecret(secret);
			openshift.loadTemplate(testContext.getProject(), secretsFile.getInputStream(), parameters);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
