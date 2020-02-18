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

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.context.wrappers.BusinessCentral;
import org.kie.cloud.tests.context.wrappers.KieServer;
import org.kie.cloud.tests.loader.Loader;
import org.kie.cloud.tests.properties.CredentialsProperties;
import org.kie.cloud.tests.utils.Deployments;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.exception.KieServerControllerHTTPClientException;

import static org.junit.Assert.fail;
import static org.kie.cloud.tests.utils.AwaitilityUtils.awaitsLong;

public interface Steps {

    Loader getCurrentLoader();

    TestContext getTestContext();

    CredentialsProperties getDefaultCredentials();

    default void assertBusinessCentralsFor(BiConsumer<KieServerControllerClient, BusinessCentral> action) {
        assertBusinessCentralsFor(getDefaultCredentials().getUser(), getDefaultCredentials().getPassword(), action);
    }

    default void assertBusinessCentralsFor(String username, String password, BiConsumer<KieServerControllerClient, BusinessCentral> action) {
        forEachBusinessCentral(deployment -> {
            awaitsLong().until(() -> {
                try {
                    KieServerControllerClient client = deployment.restClient(username, password);
                    action.accept(client, deployment);
                } catch (KieServerControllerHTTPClientException ex) {
                    if (ex.getResponseCode() > 500) {
                        // perhaps the service is not ready yet. it needs retrying
                        return false;
                    }

                    fail(String.format("User '%s:%s' cannot login in Business Central", username, password));
                }

                return true;
            });
        });
    }

    default void forEachBusinessCentral(Consumer<BusinessCentral> action) {
        forEachDeployment(name -> name.contains(Deployments.BUSINESS_CENTRAL), deployment -> action.accept(new BusinessCentral(deployment)));
    }

    default void forEachKieServer(Consumer<KieServer> action) {
        forEachDeployment(name -> name.endsWith(Deployments.KIE_SERVER), deployment -> action.accept(new KieServer(deployment)));
    }

    default void forEachDeployment(Predicate<String> match, Consumer<Deployment> action) {
        getTestContext().getDeployments().entrySet().stream().filter(entry -> match.test(entry.getKey())).forEach(entry -> action.accept(entry.getValue()));
    }
}
