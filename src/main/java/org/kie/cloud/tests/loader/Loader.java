package org.kie.cloud.tests.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.context.deployments.PostLoadDeployment;
import org.kie.cloud.tests.services.ExpressionEvaluator;
import org.kie.cloud.tests.utils.Deployments;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Loader {

    @Autowired
    private List<PostLoadDeployment> postProcessors;

    @Autowired
    protected ExpressionEvaluator evaluator;

    protected abstract List<Deployment> runLoad(TestContext testContext, String scenario, Map<String, String> extraParams);

    public void load(TestContext testContext, String scenario, Map<String, String> extraParams) {
        Map<String, String> params = new HashMap<>();
        params.putAll(extraParams);
        testContext.getProperties().forEach((k, v) -> {
            if (!params.containsKey(k)) {
                params.put(k, evaluator.resolveValue(k, v, testContext));
            }
        });
        List<Deployment> deployments = runLoad(testContext, scenario, params);
        postLoad(testContext, deployments);
    }

    protected void postLoad(TestContext testContext, List<Deployment> deployments) {
        if (deployments == null) {
            return;
        }

        deployments.forEach(deployment -> postProcessors.forEach(p -> p.process(testContext, deployment)));
    }

    protected void forEachBusinessCentral(TestContext testContext, Consumer<Deployment> action) {
        forEachDeployment(testContext, name -> name.contains(Deployments.BUSINESS_CENTRAL), deployment -> action.accept(deployment));
    }

    protected void forEachKieServer(TestContext testContext, Consumer<Deployment> action) {
        forEachDeployment(testContext, name -> name.contains(Deployments.KIE_SERVER), deployment -> action.accept(deployment));
    }

    protected void forEachDeployment(TestContext testContext, Predicate<String> match, Consumer<Deployment> action) {
        testContext.getDeployments().entrySet().stream().filter(entry -> match.test(entry.getKey())).forEach(entry -> action.accept(entry.getValue()));
    }
}
