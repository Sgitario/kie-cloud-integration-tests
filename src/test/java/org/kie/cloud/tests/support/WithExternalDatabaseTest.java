package org.kie.cloud.tests.support;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.BaseTest;
import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;
import org.springframework.beans.factory.annotation.Autowired;

public class WithExternalDatabaseTest extends BaseTest implements LoginSteps, IntegrationSteps {

    @Autowired
    private OpenshiftClient openshift;

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_KIESERVER_EXTERNALDB;
    }

    @Override
    protected Map<String, String> scenarioExtraParams() {
        Map<String, String> params = new HashMap<>();
        params.putAll(super.scenarioExtraParams());
        params.put("EXTENSIONS_IMAGE_NAMESPACE", getTestContext().getProject().getName());
        params.put("EXTENSIONS_IMAGE", "mysql-image:latest");
        params.put("KIE_SERVER_EXTERNALDB_JNDI", "java:jboss/datasources/jbpmDS");
        // params.put("KIE_SERVER_EXTERNALDB_DIALECT", "org.hibernate.dialect.MySQL57Dialect");
        params.put("KIE_SERVER_EXTERNALDB_DIALECT", "org.hibernate.dialect.MySQL5InnoDBDialect");
        params.put("KIE_SERVER_EXTERNALDB_DB", "dballo10");
        params.put("KIE_SERVER_EXTERNALDB_DRIVER", "mysql");
        params.put("KIE_SERVER_EXTERNALDB_USER", "dballo10");
        params.put("KIE_SERVER_EXTERNALDB_PWD", "dballo10");
        params.put("KIE_SERVER_EXTERNALDB_SERVICE_HOST", "mysql-57.hosts.prod.upshift.rdu2.redhat.com");
        params.put("KIE_SERVER_EXTERNALDB_SERVICE_PORT", "3306");
        // params.put("KIE_SERVER_EXTERNALDB_MIN_POOL_SIZE", "10");
        // params.put("KIE_SERVER_EXTERNALDB_MAX_POOL_SIZE", "10");
        // params.put("KIE_SERVER_EXTERNALDB_CONNECTION_CHECKER", "org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker");
        // params.put("KIE_SERVER_EXTERNALDB_EXCEPTION_SORTER", "org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter");
        return params;
    }

    @Override
    protected void beforeOnLoadScenario() {
        openshift.loadResources(getTestContext().getProject(), this.getClass().getResourceAsStream("/images/mysql-image-stream.yaml"));
    }

    @Test
    @Tag("login")
    @Tag("startup")
    public void canLogin() {
        thenCanLoginInKieServer(defaultUserName(), defaultUserPassword());
    }
}
