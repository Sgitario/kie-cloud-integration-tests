package org.kie.cloud.tests;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.clients.openshift.Project;
import org.kie.cloud.tests.config.TestConfig;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = { "classpath:openshift.properties", "classpath:test.properties",
		"classpath:templates.properties" })
@ContextConfiguration
public abstract class BaseTest {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private OpenshiftClient openshift;

	@Autowired
	private List<TestConfig> testConfigurers;

	protected TestContext testContext;

	@BeforeEach
	public void setup() {
		Project project = projectService.createProject();
		testContext = new TestContext(project);
		runConfigurers(TestConfig::before);
	}

	@AfterEach
	public void clearDown() {
		runConfigurers(TestConfig::after);
		projectService.deleteProject(testContext);
	}

	@EnableConfigurationProperties
	@Configuration
	@ComponentScan("org.kie.cloud")
	public static class Config {
	}

	protected OpenshiftClient getOpenshift() {
		return openshift;
	}

	protected String getParamContext(String paramName) {
		return testContext.getParams().get(paramName);
	}

	private void runConfigurers(BiConsumer<TestConfig, TestContext> stage) {
		try {
			for (TestConfig configurer : testConfigurers) {
				stage.accept(configurer, testContext);
			}
		} catch (Exception ex) {
			log.error("Error configuring tests", ex);
			fail("Could not configure test scenario. Cause: " + ex.getMessage());
		}
	}
}
