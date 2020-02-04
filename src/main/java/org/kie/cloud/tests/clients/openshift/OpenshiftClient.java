package org.kie.cloud.tests.clients.openshift;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cz.xtf.builder.builders.SecretBuilder;
import cz.xtf.core.openshift.OpenShift;
import cz.xtf.core.openshift.OpenShifts;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.Template;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.utils.OpenshiftExtensionModelUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.kie.cloud.tests.utils.AwaitilityUtils.awaits;
import static org.kie.cloud.tests.utils.OpenshiftExtensionModelUtils.getName;
import static org.kie.cloud.tests.utils.OpenshiftExtensionModelUtils.objectsOf;

@Slf4j
@Service
public class OpenshiftClient {

    public static final String DEPLOYMENT_CONFIG_LABEL = "deploymentconfig";
    public static final long PODS_START_TO_READY_TIMEOUT = 10 * 60 * 1000L;

    @Value("${test.dry-run:false}")
    private Boolean dryRunMode;

    public Project createProject(String projectName) {
        if (!dryRunMode) {
            loadOrCreateProject(projectName);
        }

        return new Project(projectName);
    }

    public void deleteProject(Project project) {
        if (dryRunMode) {
            return;
        }

        try (OpenShift openShift = OpenShifts.master()) {
            openShift.deleteProject(project.getName());
        }
    }

    public void createSecret(Project project, String secretName, Map<String, String> secrets) {
        if (dryRunMode) {
            return;
        }

        log.debug("Loading secret ... '{}'", secretName);
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            SecretBuilder sb = new SecretBuilder(secretName);
            for (Entry<String, String> entry : secrets.entrySet()) {
                sb.addRawData(entry.getKey(), entry.getValue());
            }

            openShift.createSecret(sb.build());
            log.debug("Secret loaded OK ");
        }
    }

    public void loadResources(Project project, InputStream is) {
        if (dryRunMode) {
            return;
        }

        log.debug("Loading resource ... ");
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            openShift.loadResource(is);
            log.debug("Resource loaded OK ");
        }
    }

    public List<Deployment> loadTemplate(Project project, InputStream is, Map<String, String> parameters) {
        if (dryRunMode) {
            return null;
        }

        log.trace("Loading template ... ");
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            Template template = openShift.loadAndCreateTemplate(is);
            openShift.processAndDeployTemplate(template.getMetadata().getName(), parameters);
            List<Deployment> deployments = objectsOf(template, DeploymentConfig.class).stream()
                    .map(deployment -> getName(template, deployment))
                    .map(name -> new Deployment(name, getDeploymentEnvironmentVariables(name, openShift)))
                    .collect(Collectors.toList());

            log.trace("Template loaded OK ");
            return deployments;
        }
    }

    public List<String> getRouteByApplication(Project project, String name) {
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            return openShift.routes().withLabel("service", name).list().getItems().stream()
                            .map(OpenshiftExtensionModelUtils::resolveRoute)
                            .collect(Collectors.toList());
        }
    }

    public void updateEnvironmentVariable(Project project, Deployment deployment, String key, String value) {
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            long currentVersion = getDeploymentLatestVersion(openShift, deployment);

            openShift.updateDeploymentConfigEnvVars(deployment.getName(), Collections.singletonMap(key, value));
            deployment.getEnvironmentVariables().clear();
            deployment.getEnvironmentVariables().putAll(getDeploymentEnvironmentVariables(deployment.getName(), openShift));

            waitForRollout(openShift, deployment, currentVersion + 1);
            waitForDeployment(project, deployment.getName());
        }

    }

    private Map<String, String> getDeploymentEnvironmentVariables(String name, OpenShift openShift) {
        Map<String, String> variables = new HashMap<>();
        awaits().until(() -> {
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

    public void waitForDeployment(Project project, String name) {
        if (dryRunMode) {
            return;
        }

        log.debug("Waiting for deployment '{}' ... ", name);
        try (OpenShift openShift = OpenShifts.master(project.getName())) {

            int expectedPods = openShift.getDeploymentConfig(name).getSpec().getReplicas().intValue();
            waitUntilAllPodsAreRunning(openShift, name, expectedPods);
            log.debug("Deployment started. ");
        }
    }

    private void waitForRollout(OpenShift openShift, Deployment deployment, long expectedVersion) {
        log.debug("Waiting for rollout '{}' ... ", deployment.getName());
        awaits().pollDelay(5, TimeUnit.SECONDS).until(() -> getDeploymentLatestVersion(openShift, deployment) == expectedVersion);

        log.debug("Rollout started. ");
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

    private void waitUntilAllPodsAreRunning(OpenShift openShift, String serviceName, int expectedPods) {
        try {
            openShift.waiters().areExactlyNPodsReady(expectedPods, DEPLOYMENT_CONFIG_LABEL, serviceName)
            .timeout(PODS_START_TO_READY_TIMEOUT).reason("Waiting for " + expectedPods
                                                         + " pods of deployment config '" + serviceName + "' to become ready.")
            .waitFor();

            openShift.waiters().areExactlyNPodsRunning(expectedPods, DEPLOYMENT_CONFIG_LABEL, serviceName)
            .timeout(PODS_START_TO_READY_TIMEOUT).reason("Waiting for " + expectedPods
                                                         + " pods of deployment config '" + serviceName + "' to become runnning.")
            .waitFor();
        } catch (AssertionError e) {
            Assertions.fail("Timeout while waiting for pods to start for service '" + serviceName + "'");
        }
    }

}
