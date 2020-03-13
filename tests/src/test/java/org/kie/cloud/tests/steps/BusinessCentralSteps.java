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

import org.kie.cloud.tests.context.wrappers.BusinessCentral;
import org.kie.cloud.tests.utils.Deployments;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.exception.KieServerControllerHTTPClientException;

import static org.junit.Assert.fail;
import static org.kie.cloud.tests.utils.AwaitilityUtils.awaitsLong;

public interface BusinessCentralSteps extends Steps {

    default void assertBusinessCentralsFor(BiConsumer<KieServerControllerClient, BusinessCentral> action) {
        forEachBusinessCentral(deployment -> {
            awaitsLong().until(() -> {
                try {
                    KieServerControllerClient client = deployment.restClient(getUserName(), getUserPassword());
                    action.accept(client, deployment);
                } catch (KieServerControllerHTTPClientException ex) {
                    if (ex.getResponseCode() > 500) {
                        // perhaps the service is not ready yet. it needs retrying
                        return false;
                    }

                    fail(String.format("User '%s:%s' cannot login in Business Central", getUserName(), getUserPassword()));
                }

                return true;
            });
        });
    }

    default void forEachBusinessCentral(Consumer<BusinessCentral> action) {
        forEachDeployment(Deployments::isBusinessCentral, deployment -> action.accept(new BusinessCentral(getTestContext(), deployment, getEnvironment())));
    }
}
