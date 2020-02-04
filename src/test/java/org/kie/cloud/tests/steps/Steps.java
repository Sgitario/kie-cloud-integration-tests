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
package org.kie.cloud.tests.steps;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.context.wrappers.BusinessCentral;
import org.kie.cloud.tests.context.wrappers.KieServer;
import org.kie.cloud.tests.loader.Loader;
import org.kie.cloud.tests.utils.Deployments;

public interface Steps {

    Loader getCurrentLoader();
    TestContext getTestContext();

    void tryAssert(Runnable action, String message);

    default void forEachBusinessCentral(Consumer<BusinessCentral> action) {
        forEachDeployment(name -> name.contains(Deployments.BUSINESS_CENTRAL), deployment -> action.accept(new BusinessCentral(deployment)));
    }

    default void forEachKieServer(Consumer<KieServer> action) {
        forEachDeployment(name -> name.contains(Deployments.KIE_SERVER), deployment -> action.accept(new KieServer(deployment)));
    }

    default void forEachDeployment(Predicate<String> match, Consumer<Deployment> action) {
        getTestContext().getDeployments().entrySet().stream().filter(entry -> match.test(entry.getKey())).forEach(entry -> action.accept(entry.getValue()));
    }
}
