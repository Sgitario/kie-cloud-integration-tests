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

import org.kie.server.controller.client.exception.KieServerControllerHTTPClientException;

import static org.junit.Assert.fail;
import static org.kie.cloud.tests.utils.AwaitilityUtils.awaitsFast;

public interface LoginSteps extends KieServerSteps, BusinessCentralSteps {

    default void thenCanLoginInBusinessCentral() {
        assertBusinessCentralsFor((c, d) -> c.listServerTemplates());
    }

    default void thenCannotLoginInBusinessCentral() {
        forEachBusinessCentral(deployment -> {
            awaitsFast().until(() -> {
                try {
                    deployment.restClient(getUserName(), getUserPassword()).listServerTemplates();
                    fail(String.format("User '%s:%s' can login in Business Central", getUserName(), getUserPassword()));
                } catch (KieServerControllerHTTPClientException ex) {
                    if (ex.getResponseCode() == 401) {
                        return true;
                    }
                }

                // perhaps the service is not ready yet. it needs retrying
                return false;
            });
        });
    }

    default void thenCanLoginInKieServer() {
        assertKieServersFor((c, d) -> c.listContainers());
        thenKieServersStartUpOk();
    }

}
