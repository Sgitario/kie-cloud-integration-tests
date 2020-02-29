package org.kie.cloud.tests.loader.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.cloud.tests.core.context.Deployment;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.core.properties.CredentialsProperties;
import org.kie.cloud.tests.loader.Loader;
import org.kie.cloud.tests.loader.operator.environment.OperatorOpenshiftLoader;
import org.kie.cloud.tests.loader.operator.mappers.KieAppPopulator;
import org.kie.cloud.tests.loader.operator.model.CommonConfig;
import org.kie.cloud.tests.loader.operator.model.Console;
import org.kie.cloud.tests.loader.operator.model.Env;
import org.kie.cloud.tests.loader.operator.model.KieApp;
import org.kie.cloud.tests.loader.operator.model.Server;
import org.kie.cloud.tests.utils.environment.OpenshiftEnvironment;
import org.slf4j.MDC;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@Component("operator")
@RequiredArgsConstructor
public class OperatorLoader extends Loader {

    private static final String MDC_CURRENT_RESOURCE = "resource";
    private static final String CUSTOM_RESOURCE_DEFINITION = "kieapps.app.kiegroup.org";
    private static final String KIE_ADMIN_USER = "KIE_ADMIN_USER";
    private static final String KIE_ADMIN_PWD = "KIE_ADMIN_PWD";

    private final OperatorConfiguration configuration;
    private final OperatorOpenshiftLoader loader;
    private final OpenshiftEnvironment environment;
    private final CredentialsProperties credentials;

    private final List<KieAppPopulator> populators;

    @Override
    protected List<Deployment> runLoad(TestContext testContext, String scenario, Map<String, String> extraParams) {
        OperatorDefinition definition = loadOperatorDefinition(scenario);
        importCrd(testContext);
        importServiceAccount(testContext);
        importRole(testContext);
        importRoleBinding(testContext);
        importOperator(testContext);
        runOperator(testContext, environment(scenario, definition), extraParams);
        return loadDeployments(testContext, definition);
    }

    private String environment(String scenario, OperatorDefinition definition) {
        if (definition == null) {
            return scenario;
        }

        return Optional.ofNullable(definition.getOverrideEnvironment()).orElse(scenario);
    }

    private OperatorDefinition loadOperatorDefinition(String scenario) {
        return configuration.getDefinitions().get(scenario);
    }

    private void runOperator(TestContext testContext, String environmentName, Map<String, String> extraParams) {
        KieApp app = new KieApp();
        app.getMetadata().setNamespace(testContext.getProject().getName());
        app.getMetadata().setName(configuration.getKieAppName());
        app.getSpec().setEnvironment(environmentName);
        app.getSpec().setUseImageTags(true);

        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setAdminUser(credentials.getUser());
        commonConfig.setAdminPassword(credentials.getPassword());
        commonConfig.setApplicationName(configuration.getKieAppName());
        app.getSpec().setCommonConfig(commonConfig);

        List<Env> authenticationEnvVars = new ArrayList<>();
        authenticationEnvVars.add(new Env(KIE_ADMIN_USER, commonConfig.getAdminUser()));
        authenticationEnvVars.add(new Env(KIE_ADMIN_PWD, commonConfig.getAdminPassword()));

        Server server = new Server();
        server.addEnvs(authenticationEnvVars);
        app.getSpec().getObjects().addServer(server);

        Console console = new Console();
        console.addEnvs(authenticationEnvVars);
        app.getSpec().getObjects().setConsole(console);

        populators.forEach(p -> p.populate(app, extraParams));
        loader.loadOperator(testContext, app);
    }

    private List<Deployment> loadDeployments(TestContext testContext, OperatorDefinition definition) {
        if (definition == null) {
            return Collections.emptyList();
        }

        return definition.getDeployments().parallelStream().map(partialName -> prepareDeployment(testContext, partialName)).collect(Collectors.toList());
    }

    private Deployment prepareDeployment(TestContext testContext, String partialNameDeployment) {
        String deploymentName = String.format("%s-%s", configuration.getKieAppName(), partialNameDeployment);
        Deployment deployment = environment.getDeployment(testContext.getProject(), deploymentName);
        ensureRouteHttp(testContext, deploymentName);
        return deployment;
    }

    private void ensureRouteHttp(TestContext testContext, String deploymentName) {
        List<String> routes = environment.getRouteByApplication(testContext.getProject(), deploymentName);
        if (!routes.isEmpty() && routes.stream().noneMatch(route -> route.startsWith("http:"))) {
            String targetHost = routes.get(0).replaceAll("https://", "insecure-");
            environment.createRouteForService(testContext.getProject(), "http", targetHost, deploymentName);
        }
    }

    private void importServiceAccount(TestContext testContext) {
        load(testContext, configuration.getServiceAccount());
    }

    private void importRole(TestContext testContext) {
        load(testContext, configuration.getRole());
    }

    private void importRoleBinding(TestContext testContext) {
        load(testContext, configuration.getRoleBinding());
    }

    private void importOperator(TestContext testContext) {
        load(testContext, configuration.getDefinition());
    }

    private void importCrd(TestContext testContext) {
        if (configuration.isForceCrdUpdate()) {
            load(testContext, configuration.getCrd());
        } else {
            loadGlobalCrdIfNotExists();
        }
    }

    private void loadGlobalCrdIfNotExists() {
        try {
            environment.createGlobalCustomResourceDefinition(CUSTOM_RESOURCE_DEFINITION, configuration.getCrd().getInputStream());
        } catch (IOException e) {
            log.error("Error loading crd", e);
            fail("Could not load crd. Cause: " + e.getMessage());
        }
    }

    private void load(TestContext testContext, Resource resource) {
        try {
            MDC.put(MDC_CURRENT_RESOURCE, resource.getFilename());
            environment.createResource(testContext.getProject(), resource.getInputStream());
        } catch (IOException e) {
            log.error("Error loading resource", e);
            fail("Could not load resource. Cause: " + e.getMessage());
        } finally {
            MDC.remove(MDC_CURRENT_RESOURCE);
        }
    }

}
