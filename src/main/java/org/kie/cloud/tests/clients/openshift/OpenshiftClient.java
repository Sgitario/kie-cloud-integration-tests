package org.kie.cloud.tests.clients.openshift;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import cz.xtf.builder.builders.RouteBuilder;
import cz.xtf.builder.builders.SecretBuilder;
import cz.xtf.core.openshift.OpenShift;
import cz.xtf.core.openshift.OpenShifts;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.Template;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.kie.cloud.tests.config.operators.KieApp;
import org.kie.cloud.tests.config.operators.KieAppDoneable;
import org.kie.cloud.tests.config.operators.KieAppList;
import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.utils.OpenshiftExtensionModelUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.kie.cloud.tests.utils.AwaitilityUtils.awaitsLong;
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
                    .map(name -> loadDeployment(openShift, name))
                    .collect(Collectors.toList());

            log.trace("Template loaded OK ");
            return deployments;
        }
    }

    public Deployment loadDeployment(Project project, String name) {
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

    public List<String> getRouteByApplication(Project project, String name) {
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            return openShift.routes().withLabel("service", name).list().getItems().stream()
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

    public void createRouteForService(Project project, String name, String host, String deploymentName) {
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            openShift.createRoute(new RouteBuilder(deploymentName + "-" + name).addLabel("service", deploymentName).forService(deploymentName).exposedAsHost(host).build());
        }
    }

    public KieApp loadOperator(Project project, KieApp app) {
        return operatorClient(project).create(app);
    }

    public void updateOperator(Project project, String app, Consumer<KieApp> actions) {
        KieAppDoneable operator = operatorClient(project).withName(app).edit();
        actions.accept(operator.getResource());
        operator.done();
    }

    public void waitForDeployment(Project project, String name) {
        if (dryRunMode) {
            return;
        }

        log.debug("Waiting for deployment '{}' ... ", name);
        try (OpenShift openShift = OpenShifts.master(project.getName())) {

            waitForDeployment(openShift, name);
            log.debug("Deployment started. ");
        }
    }

    public void waitForRollout(Project project, Collection<Deployment> deployments) {
        try (OpenShift openShift = OpenShifts.master(project.getName())) {
            deployments.parallelStream().forEach(deployment -> waitForRollout(openShift, deployment));
        }
    }

    private void waitForRollout(OpenShift openShift, Deployment deployment) {
        log.debug("Waiting for rollout '{}' ... ", deployment.getName());
        deployment.setVersion(awaitsLong().pollDelay(5, TimeUnit.SECONDS).until(() -> getDeploymentLatestVersion(openShift, deployment), Matchers.greaterThan(deployment.getVersion())));
        waitForDeployment(openShift, deployment.getName());
        log.debug("Rollout started. ");
    }

    private void waitForDeployment(OpenShift openShift, String name) {
        awaitsLong().pollDelay(5, TimeUnit.SECONDS).until(() -> openShift.getDeploymentConfig(name) != null);
        int expectedPods = openShift.getDeploymentConfig(name).getSpec().getReplicas().intValue();
        waitUntilAllPodsAreRunning(openShift, name, expectedPods);
    }

    private Deployment loadDeployment(OpenShift openShift, String name) {
        Deployment deployment = new Deployment(name, getDeploymentEnvironmentVariables(openShift, name));
        deployment.setVersion(getDeploymentLatestVersion(openShift, deployment));
        return deployment;
    }

    private NonNamespaceOperation<KieApp, KieAppList, KieAppDoneable, Resource<KieApp, KieAppDoneable>> operatorClient(Project project) {
        CustomResourceDefinition customResourceDefinition = OpenShifts.admin().customResourceDefinitions().withName("kieapps.app.kiegroup.org").get();
        return OpenShifts.admin().customResources(customResourceDefinition, KieApp.class, KieAppList.class, KieAppDoneable.class).inNamespace(project.getName());
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
