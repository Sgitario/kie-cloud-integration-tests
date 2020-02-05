package org.kie.cloud.tests.loader;

import java.util.List;
import java.util.Map;

import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.context.deployments.PostLoadDeployment;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Loader {

    @Autowired
    private List<PostLoadDeployment> postProcessors;

    public abstract void load(TestContext testContext, String template, Map<String, String> extraParams);

    public abstract void whenSetExternalAuthTo(TestContext testContext, boolean value);

    protected void postLoad(TestContext testContext, List<Deployment> deployments) {
        if (deployments == null) {
            return;
        }

        deployments.forEach(deployment -> postProcessors.forEach(p -> p.process(testContext, deployment)));
    }
}
