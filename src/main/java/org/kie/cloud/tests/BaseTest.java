package org.kie.cloud.tests;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.cloud.tests.clients.openshift.Project;
import org.kie.cloud.tests.config.TestConfig;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.loader.Loader;
import org.kie.cloud.tests.properties.CredentialsProperties;
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
@TestPropertySource(locations = {"classpath:params.properties", "classpath:test.properties", "classpath:ldap.properties", "classpath:parties.properties"})
@ContextConfiguration
@TestInstance(Lifecycle.PER_CLASS)
public abstract class BaseTest {

	@Autowired
	private ProjectService projectService;

    @Autowired
    private CredentialsProperties defaultCredentials;

	@Autowired
	private List<TestConfig> testConfigurers;

    @Value("${test.loaders.selected}")
    private String loaderClass;

	@Value("${test.project.on.test.after.delete}")
	private boolean deleteProjectAfter;

    @Autowired
    private ApplicationContext appContext;

    @Getter
    private TestContext testContext;

    @Getter
    private Loader currentLoader;

    @BeforeAll
	public void setup() {
		Project project = projectService.createProject();
		testContext = new TestContext(project);
		runConfigurers(TestConfig::before);
        beforeOnLoadScenario();
        whenLoadTemplate(scenario(), scenarioExtraParams());
        afterOnLoadScenario();
	}

    @AfterAll
	public void clearDown() {
		runConfigurers(TestConfig::after);

		if (deleteProjectAfter) {
			projectService.deleteProject(testContext);
		}
	}

    protected abstract String scenario();

    protected void beforeOnLoadScenario() {

    }

    protected void afterOnLoadScenario() {

    }

    protected Map<String, String> scenarioExtraParams() {
        return Collections.emptyMap();
    }

    protected void whenLoadTemplate(String scenario, Map<String, String> extraParams) {
        currentLoader = appContext.getBean(loaderClass, Loader.class);
        currentLoader.load(testContext, scenario, extraParams);
    }

	@EnableConfigurationProperties
	@Configuration
	@ComponentScan("org.kie.cloud")
	public static class Config {
	}

    protected String defaultUserName() {
        return defaultCredentials.getUser();
    }

    protected String defaultUserPassword() {
        return defaultCredentials.getPassword();
    }

    protected String projectName() {
        return testContext.getProject().getName();
    }

	protected String getDeploymentParam(String deploymentName, String paramName) {
		return testContext.getDeployment(deploymentName).getEnvironmentVariable(paramName);
	}

    protected Map<String, String> getDeploymentParams(String deploymentName) {
        return testContext.getDeployment(deploymentName).getEnvironmentVariables();
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
