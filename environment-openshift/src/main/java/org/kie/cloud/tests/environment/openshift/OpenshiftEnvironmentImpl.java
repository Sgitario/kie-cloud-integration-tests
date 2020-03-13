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
package org.kie.cloud.tests.environment.openshift;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cz.xtf.builder.builders.RouteBuilder;
import cz.xtf.builder.builders.SecretBuilder;
import cz.xtf.core.openshift.OpenShift;
import cz.xtf.core.openshift.OpenShifts;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.kie.cloud.tests.core.context.Deployment;
import org.kie.cloud.tests.core.context.Project;
import org.kie.cloud.tests.environment.Environment;
import org.kie.cloud.tests.utils.OpenshiftExtensionModelUtils;
import org.kie.cloud.tests.utils.environment.OpenshiftEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.kie.cloud.tests.utils.AwaitilityUtils.awaitsLong;
import static org.kie.cloud.tests.utils.OpenshiftExtensionModelUtils.getName;
import static org.kie.cloud.tests.utils.OpenshiftExtensionModelUtils.objectsOf;

@Slf4j
@Component
public class OpenshiftEnvironmentImpl implements OpenshiftEnvironment, Environment {

    public static final String DEPLOYMENT_CONFIG_LABEL = "deploymentconfig";
    public static final long PODS_START_TO_READY_TIMEOUT = 10 * 60 * 1000L;

    @Value("${test.dry-run:false}")
    private Boolean dryRunMode;

    @Override
    public Project createProject(String projectName) {
        if (!dryRunMode) {
            loadOrCreateProject(projectName);
        }

        return new Project(projectName);
    }

    @Override
    public void deleteProject(Project project) {
        if (dryRunMode) {
            return;
        }

        try (OpenShift openShift = OpenShifts.master()) {
            openShift.deleteProject(project.getName());
        }
    }

    @Override
    public void restartDeployment(Project project, Deployment deployment) {
        if (dryRunMode) {
            return;
        }

        log.debug("Restarting deployment ...  {}", deployment.getName());
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            int replicas = getReplicasCountByDeployment(openShift, deployment.getName());
            openShift.deploymentConfigs().withName(deployment.getName()).scale(0);
            waitForDeployment(openShift, deployment.getName());
            openShift.deploymentConfigs().withName(deployment.getName()).scale(replicas, true);
            waitForDeployment(openShift, deployment.getName());
            log.debug("Deployment restarted OK ");
        }
    }

    public void createRawSecret(Project project, String secretName, Map<String, String> secrets) {
        createSecret(project, secretName, () -> {
            SecretBuilder sb = new SecretBuilder(secretName);
            for (Entry<String, String> entry : secrets.entrySet()) {
                sb.addRawData(entry.getKey(), entry.getValue());
            }

            return sb;
        });
    }

    public void createEncodedSecret(Project project, String secretName, Map<String, String> secrets) {
        createSecret(project, secretName, () -> {
            SecretBuilder sb = new SecretBuilder(secretName);
            for (Entry<String, String> entry : secrets.entrySet()) {
                sb.addEncodedData(entry.getKey(), entry.getValue());
            }

            return sb;
        });
    }

    @Override
    public void createGlobalCustomResourceDefinition(String name, InputStream is) {
        if (dryRunMode) {
            return;
        }

        try (OpenShift openShift = OpenShifts.master()) {
            CustomResourceDefinition crd = openShift.customResourceDefinitions().withName(name).get();
            if (crd == null) {
                log.debug("Loading custom resource definition ... ");
                openShift.loadResource(is);
                log.debug("Custom resource definition loaded OK ");
            }
        }
    }

    @Override
    public void createResource(Project project, InputStream is) {
        if (dryRunMode) {
            return;
        }

        log.debug("Loading resource ... ");
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            openShift.loadResource(is);
            log.debug("Resource loaded OK ");
        }
    }

    @Override
    public List<Deployment> createTemplate(Project project, InputStream is, Map<String, String> parameters) {
        if (dryRunMode) {
            return null;
        }

        log.trace("Loading template ... ");
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            Template template = openShift.loadAndCreateTemplate(is);
            openShift.processAndDeployTemplate(template.getMetadata().getName(), parameters);
            List<Deployment> deployments = objectsOf(template, DeploymentConfig.class).stream()
                                                                                      .map(deployment -> getName(template, deployment, parameters))
                                                                                      .map(name -> loadDeployment(openShift, name))
                                                                                      .collect(Collectors.toList());

            log.trace("Template loaded OK ");
            return deployments;
        }
    }

    @Override
    public Deployment getDeployment(Project project, String name) {
        waitForDeployment(project, name);

        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            return loadDeployment(openShift, name);
        }

    }

    public io.fabric8.kubernetes.api.model.Service getServiceByApplication(Project project, String name) {
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            return openShift.services().withName(name).get();
        }
    }

    @Override
    public List<String> getRouteByApplication(Project project, String name) {
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            return openShift.getRoutes().stream()
                            .filter(route -> StringUtils.endsWithIgnoreCase(route.getSpec().getTo().getName(), name))
                            .map(OpenshiftExtensionModelUtils::resolveRoute)
                            .collect(Collectors.toList());
        }
    }

    public void updateEnvironmentVariable(Project project, Deployment deployment, String key, String value) {
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            openShift.updateDeploymentConfigEnvVars(deployment.getName(), Collections.singletonMap(key, value));
            deployment.getEnvironmentVariables().clear();
            deployment.getEnvironmentVariables().putAll(getDeploymentEnvironmentVariables(openShift, deployment.getName()));

            waitForRollout(openShift, deployment);
        }

    }

    @Override
    public void createRouteForService(Project project, String name, String host, String deploymentName) {
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            openShift.createRoute(new RouteBuilder(deploymentName + "-" + name).addLabel("service", deploymentName).forService(deploymentName).exposedAsHost(host).build());
        }
    }

    public void waitForDeployment(Project project, String deploymentName) {
        if (dryRunMode) {
            return;
        }

        log.debug("Waiting for deployment '{}' ... ", deploymentName);
        try (OpenShift openShift = OpenShifts.master(project.getName())) {

            waitForDeployment(openShift, deploymentName);
            log.debug("Deployment '{}' started. ", deploymentName);
        }
    }

    private void createSecret(Project project, String secretName, Supplier<SecretBuilder> builder) {
        if (dryRunMode) {
            return;
        }

        log.debug("Loading secret ... '{}'", secretName);
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            openShift.createSecret(builder.get().build());
            log.debug("Secret loaded OK ");
        }
    }

    private void waitForRollout(OpenShift openShift, Deployment deployment) {
        log.debug("Waiting for rollout '{}' ... ", deployment.getName());
        deployment.setVersion(awaitsLong().pollDelay(5, TimeUnit.SECONDS).until(() -> getDeploymentLatestVersion(openShift, deployment), Matchers.greaterThan(deployment.getVersion())));
        waitForDeployment(openShift, deployment.getName());
        log.debug("Rollout started. ");
    }

    private void waitForDeployment(OpenShift openShift, String name) {
        int expectedPods = getReplicasCountByDeployment(openShift, name);
        waitUntilAllPodsAreRunning(openShift, name, expectedPods);
    }

    private int getReplicasCountByDeployment(OpenShift openShift, String name) {
        return openShift.getDeploymentConfig(name).getSpec().getReplicas().intValue();
    }

    private Deployment loadDeployment(OpenShift openShift, String name) {
        Deployment deployment = new Deployment(name, getDeploymentEnvironmentVariables(openShift, name));
        deployment.setVersion(getDeploymentLatestVersion(openShift, deployment));
        return deployment;
    }

    private Map<String, String> getDeploymentEnvironmentVariables(OpenShift openShift, String name) {
        Map<String, String> variables = new HashMap<>();
        awaitsLong().until(() -> {
            try {
                variables.clear();
                variables.putAll(openShift.getDeploymentConfigEnvVars(name));
                return true;
            } catch (Exception ex) {

                log.warn("Error loading config properties. Cause: " + ex.getMessage());
            }

            return false;
        });

        return variables;
    }

    private long getDeploymentLatestVersion(OpenShift openShift, Deployment deployment) {
        return openShift.getDeploymentConfig(deployment.getName()).getStatus().getLatestVersion();
    }

    private void loadOrCreateProject(String projectName) {
        try (OpenShift openShift = OpenShifts.master()) {
            if (openShift.getProject(projectName) == null) {
                log.trace("Creating project ... ");
                openShift.createProjectRequest(projectName);
                log.trace("Project created OK.");
            } else {
                log.debug("Project already exists. ");
            }
        }
    }

    private void waitUntilAllPodsAreRunning(OpenShift openShift, String name, int expectedPods) {
        try {
            openShift.waiters().areExactlyNPodsReady(expectedPods, DEPLOYMENT_CONFIG_LABEL, name)
                     .timeout(PODS_START_TO_READY_TIMEOUT).reason("Waiting for " + expectedPods + " pods of deployment config '" + name + "' to become ready.")
                     .waitFor();

            openShift.waiters().areExactlyNPodsRunning(expectedPods, DEPLOYMENT_CONFIG_LABEL, name)
                     .timeout(PODS_START_TO_READY_TIMEOUT).reason("Waiting for " + expectedPods + " pods of deployment config '" + name + "' to become runnning.")
                     .waitFor();
        } catch (AssertionError e) {
            Assertions.fail("Timeout while waiting for pods to start for service '" + name + "'");
        }
    }
}
