package org.kie.cloud.tests.loader;

import java.io.IOException;
import java.util.Map;

import cz.xtf.core.openshift.OpenShifts;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.config.operators.CommonConfig;
import org.kie.cloud.tests.config.operators.KieApp;
import org.kie.cloud.tests.config.operators.KieAppDoneable;
import org.kie.cloud.tests.config.operators.KieAppList;
import org.kie.cloud.tests.config.operators.OperatorConfiguration;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.properties.CredentialsProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@Component("operator")
@RequiredArgsConstructor
public class OperatorLoader implements Loader {

    private static final String MDC_CURRENT_RESOURCE = "resource";

    private final OperatorConfiguration configuration;
    private final OpenshiftClient openshiftClient;
    private final CredentialsProperties credentials;

    @Override
    public void load(TestContext testContext, String template, Map<String, String> extraParams) {
        importCrd(testContext);
        importServiceAccount(testContext);
        importRole(testContext);
        importRoleBinding(testContext);
        importOperator(testContext);
        runOperator(testContext, template, extraParams);
    }

    @Override
    public void whenSetExternalAuthTo(TestContext testContext, boolean value) {
        // TODO Auto-generated method stub

    }

    private void runOperator(TestContext testContext, String template, Map<String, String> extraParams) {
        KieApp app = new KieApp();
        app.getMetadata().setName(configuration.getKieAppName());
        app.getSpec().setEnvironment(template);

        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setAdminUser(credentials.getUser());
        commonConfig.setAdminPassword(credentials.getPassword());
        app.getSpec().setCommonConfig(commonConfig);

        getKieAppClient(testContext).create(app);
    }

    private NonNamespaceOperation<KieApp, KieAppList, KieAppDoneable, io.fabric8.kubernetes.client.dsl.Resource<KieApp, KieAppDoneable>> getKieAppClient(TestContext testContext) {
        CustomResourceDefinition customResourceDefinition = OpenShifts.admin().customResourceDefinitions().withName("kieapps.app.kiegroup.org").get();
        return OpenShifts.admin().customResources(customResourceDefinition, KieApp.class, KieAppList.class, KieAppDoneable.class).inNamespace(testContext.getProject().getName());
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
