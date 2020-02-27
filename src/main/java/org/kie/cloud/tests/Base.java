/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.cloud.tests;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kie.cloud.tests.clients.openshift.Project;
import org.kie.cloud.tests.config.TestConfig;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.loader.Loader;
import org.kie.cloud.tests.properties.CredentialsProperties;
import org.kie.cloud.tests.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public abstract class Base {

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

    protected void startUp() {
        Project project = projectService.createProject();
        testContext = new TestContext(project);
        runConfigurers(TestConfig::before);
    }

    protected void cleanUp() {
        runConfigurers(TestConfig::after);

        if (deleteProjectAfter) {
            projectService.deleteProject(testContext);
        }
    }

    protected void whenLoadScenario(String scenario, Map<String, String> extraParams) {
        currentLoader = appContext.getBean(loaderClass, Loader.class);
        currentLoader.load(testContext, scenario, extraParams);
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
