package org.kie.cloud.tests;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.cloud.tests.services.RecordScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"classpath:params.properties", "classpath:test.properties", "classpath:ldap.properties", "classpath:parties.properties"})
@ContextConfiguration
@TestInstance(Lifecycle.PER_CLASS)
public abstract class BaseTest extends Base {

    @Autowired
    private RecordScenarioService recordScenario;

    @BeforeAll
	public void setup() {
        super.startUp();
        beforeOnLoadScenario();
        whenLoadScenario(scenario(), scenarioExtraParams());
        afterOnLoadScenario();
	}

    @AfterAll
	public void clearDown() {
        super.cleanUp();
	}

    protected abstract String scenario();

    protected void beforeOnLoadScenario() {
        recordScenario.record(this.getClass().getName(), scenario(), scenarioExtraParams());
    }

    protected void afterOnLoadScenario() {

    }

    protected Map<String, String> scenarioExtraParams() {
        return Collections.emptyMap();
    }

	@EnableConfigurationProperties
	@Configuration
	@ComponentScan("org.kie.cloud")
	public static class Config {
	}

}
