package org.kie.cloud.tests.loader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.config.operators.Auth;
import org.kie.cloud.tests.config.operators.CommonConfig;
import org.kie.cloud.tests.config.operators.KieApp;
import org.kie.cloud.tests.config.operators.OperatorConfiguration;
import org.kie.cloud.tests.config.operators.mappers.KieAppPopulator;
import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.properties.CredentialsProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@Component("operator")
@RequiredArgsConstructor
public class OperatorLoader extends Loader {

    private static final String MDC_CURRENT_RESOURCE = "resource";

    private final OperatorConfiguration configuration;
    private final OpenshiftClient openshiftClient;
    private final CredentialsProperties credentials;

    private final List<KieAppPopulator> populators;

    @Override
    public void load(TestContext testContext, String template, Map<String, String> extraParams) {
        importCrd(testContext);
        importServiceAccount(testContext);
        importRole(testContext);
        importRoleBinding(testContext);
        importOperator(testContext);
        runOperator(testContext, template, extraParams);
        waitForDeployments(testContext, template);
    }

    @Override
    public void whenSetExternalAuthTo(TestContext testContext, boolean value) {
        openshiftClient.updateOperator(testContext.getProject(), configuration.getKieAppName(), app -> {
            Auth auth = app.getSpec().getAuth();
            if (auth == null) {
                auth = new Auth();
                app.getSpec().setAuth(auth);
            }

            auth.setExternalOnly(value);
        });
        openshiftClient.waitForRollout(testContext.getProject(), testContext.getDeployments().values());
    }

    private void waitForDeployments(TestContext testContext, String template) {
        postLoad(testContext, loadDeployments(testContext, template));
    }

    private void runOperator(TestContext testContext, String template, Map<String, String> extraParams) {
        KieApp app = new KieApp();
        app.getMetadata().setNamespace(testContext.getProject().getName());
        app.getMetadata().setName(configuration.getKieAppName());
        app.getSpec().setEnvironment(template);

        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setAdminUser(credentials.getUser());
        commonConfig.setAdminPassword(credentials.getPassword());
        app.getSpec().setCommonConfig(commonConfig);

        populators.forEach(p -> p.populate(app, extraParams));
        openshiftClient.loadOperator(testContext.getProject(), app);
    }

    private List<Deployment> loadDeployments(TestContext testContext, String template) {
        return configuration.getDeployments().get(template).parallelStream().map(partialName -> prepareDeployment(testContext, partialName)).collect(Collectors.toList());
    }

    private Deployment prepareDeployment(TestContext testContext, String partialNameDeployment) {
        String deploymentName = String.format("%s-%s", configuration.getKieAppName(), partialNameDeployment);
        Deployment deployment = openshiftClient.loadDeployment(testContext.getProject(), deploymentName);
        ensureRouteHttp(testContext, deploymentName);
        return deployment;
    }

    private void ensureRouteHttp(TestContext testContext, String deploymentName) {
        List<String> routes = openshiftClient.getRouteByApplication(testContext.getProject(), deploymentName);
        if (routes.stream().noneMatch(route -> route.startsWith("http:"))) {
            String targetHost = routes.get(0).replaceAll("https://", "insecure-");
            openshiftClient.createRouteForService(testContext.getProject(), "http", targetHost, deploymentName);
        }
    }

    private void importCrd(TestContext testContext) {
        load(testContext, configuration.getCrd());
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

    private void load(TestContext testContext, Resource resource) {
        try {
            MDC.put(MDC_CURRENT_RESOURCE, resource.getFilename());
            openshiftClient.loadResources(testContext.getProject(), resource.getInputStream());
        } catch (IOException e) {
            log.error("Error loading resource", e);
            fail("Could not load resource. Cause: " + e.getMessage());
        } finally {
            MDC.remove(MDC_CURRENT_RESOURCE);
        }
    }

}
