package org.kie.cloud.tests.loader.operator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import cz.xtf.core.openshift.OpenShift;
import cz.xtf.core.openshift.OpenShifts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.kie.cloud.tests.core.constants.VersionConstants;
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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@Component("operator")
@RequiredArgsConstructor
public class OperatorLoader extends Loader {

    private static final String CUSTOM_RESOURCE_DEFINITION = "kieapps.app.kiegroup.org";
    private static final String OPERATOR_IMAGE = "rhpam-rhel8-operator";
    private static final String KIE_ADMIN_USER = "KIE_ADMIN_USER";
    private static final String KIE_ADMIN_PWD = "KIE_ADMIN_PWD";

    private final OperatorConfiguration configuration;
    private final OperatorOpenshiftLoader loader;
    private final OpenshiftEnvironment environment;
    private final CredentialsProperties credentials;

    private final List<KieAppPopulator> populators;

    public void changeOperatorVersion(TestContext testContext, String toVersion) {
        String imageVersionUrl = environment.getImageStreamUrlByVersion(testContext.getProject(), OPERATOR_IMAGE, toVersion);

        try (OpenShift openShift = OpenShifts.master(testContext.getProject().getName())) {
            openShift.apps().deployments().withName("kie-cloud-operator").edit()
                     .editSpec()
                         .editTemplate()
                             .editSpec()
                                 .editFirstContainer()
                                     .withNewImage(imageVersionUrl)
                                 .endContainer()
                             .endSpec()
                         .endTemplate()
                     .endSpec()
                     .done();
        }
    }

    @Override
    protected List<Deployment> runLoad(TestContext testContext, String scenario, Map<String, String> extraParams) {
        OperatorDefinition definition = loadOperatorDefinition(scenario);
        importCrd(testContext);
        importServiceAccount(testContext);
        importRoles(testContext);
        importRoleBindings(testContext);
        importOperator(testContext, extraParams);
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
        app.getSpec().setVersion(configuration.getUseComponentsVersion());

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
        server.addEnv(new Env("KIE_SERVER_CONTROLLER_OPENSHIFT_PREFER_KIESERVER_SERVICE", "false"));
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
        load(testContext, toInputStream(configuration.getServiceAccount()));
    }

    private void importRoles(TestContext testContext) {
        configuration.getRoles().forEach(role -> load(testContext, toInputStream(role)));
    }

    private void importRoleBindings(TestContext testContext) {
        configuration.getRoleBindings().stream()
                     .map(this::toInputStream)
                     .map(is -> read(is).replaceAll("namespace: placeholder", "namespace: " + testContext.getProject().getName()))
                     .forEach(content -> {
                         load(testContext, new ByteArrayInputStream(content.getBytes()));
        });
    }

    private void importOperator(TestContext testContext, Map<String, String> extraParams) {
        InputStream is = toInputStream(configuration.getDefinition());
        String imageVersionUrl = environment.getLatestImageStreamUrl(testContext.getProject(), OPERATOR_IMAGE);
        String overridesVersion = extraParams.get(VersionConstants.OVERRIDES_VERSION);
        if (overridesVersion != null) {
            imageVersionUrl = environment.getImageStreamUrlByVersion(testContext.getProject(), OPERATOR_IMAGE, overridesVersion);
        }

        String content = read(is).replaceAll("image: .+\n", String.format("image: %s\n", imageVersionUrl));
        load(testContext, new ByteArrayInputStream(content.getBytes()));
    }

    private String read(InputStream is) {
        try {
            return IOUtils.toString(is, Charsets.UTF_8);
        } catch (IOException e) {
            log.error("Error reading input stream", e);
            fail("Error reading input stream. Cause: " + e.getMessage());
        }

        return null;
    }

    private void importCrd(TestContext testContext) {
        if (configuration.isForceCrdUpdate()) {
            load(testContext, toInputStream(configuration.getCrd()));
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

    private InputStream toInputStream(Resource resource) {
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            log.error("Error loading resource", e);
            fail("Could not load resource. Cause: " + e.getMessage());
        }

        return null;
    }

    private void load(TestContext testContext, InputStream is) {
        environment.createResource(testContext.getProject(), is);
    }

}
