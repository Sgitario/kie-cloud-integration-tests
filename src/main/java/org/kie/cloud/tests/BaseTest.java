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
import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.clients.openshift.Project;
import org.kie.cloud.tests.config.TestConfig;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.loader.Loader;
import org.kie.cloud.tests.services.KieServerControllerClientService;
import org.kie.cloud.tests.services.KieServerExecutionClientService;
import org.kie.cloud.tests.services.ProjectService;
import org.kie.cloud.tests.utils.AwaitilityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"classpath:openshift.properties", "classpath:test.properties"})
@ContextConfiguration
@TestInstance(Lifecycle.PER_CLASS)
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

    @Getter
    @Autowired
    private KieServerControllerClientService kieServerControllerClientService;

    @Getter
    @Autowired
    private KieServerExecutionClientService kieServerExecutionClientService;

    private TestContext testContext;

    @BeforeAll
	public void setup() {
		Project project = projectService.createProject();
		testContext = new TestContext(project);
		runConfigurers(TestConfig::before);
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

    public void tryAssert(Runnable action, String message) {
        AwaitilityUtils.awaits().until(() -> {
            try {
                action.run();
                assertTrue(true);
            } catch (Exception ex) {
                fail(message);
                return false;
            }

            return true;
        });
    }

    protected abstract String scenario();

    protected void afterOnLoadScenario() {

    }

    protected Map<String, String> scenarioExtraParams() {
        return Collections.emptyMap();
    }

    protected void whenLoadTemplate(String scenario, Map<String, String> extraParams) {
        appContext.getBean(loaderClass, Loader.class).load(testContext, scenario, extraParams);
    }

	@EnableConfigurationProperties
	@Configuration
	@ComponentScan("org.kie.cloud")
	public static class Config {
	}

    protected String projectName() {
        return testContext.getProject().getName();
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
