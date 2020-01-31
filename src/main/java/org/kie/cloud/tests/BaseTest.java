package org.kie.cloud.tests;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.clients.openshift.Project;
import org.kie.cloud.tests.config.TestConfig;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.loader.Loader;
import org.kie.cloud.tests.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"classpath:openshift.properties", "classpath:test.properties"})
@ContextConfiguration
public abstract class BaseTest {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private OpenshiftClient openshift;

	@Autowired
	private List<TestConfig> testConfigurers;

    @Value("${test.loaders.selected}")
    private String loaderClass;

	@Value("${test.project.on.test.after.delete}")
	private boolean deleteProjectAfter;

    @Autowired
    private ApplicationContext appContext;

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

		if (deleteProjectAfter) {
			projectService.deleteProject(testContext);
		}
	}

    protected void whenLoadTemplate(String template) {
        whenLoadTemplate(template, Collections.emptyMap());
    }

    protected void whenLoadTemplate(String template, Map<String, String> extraParams) {
        appContext.getBean(loaderClass, Loader.class).load(testContext, template, extraParams);
    }

	@EnableConfigurationProperties
	@Configuration
	@ComponentScan("org.kie.cloud")
	public static class Config {
	}

	protected OpenshiftClient getOpenshift() {
		return openshift;
	}

	protected String getDeploymentParam(String deploymentName, String paramName) {
		return testContext.getDeployment(deploymentName).getEnvironmentVariable(paramName);
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
