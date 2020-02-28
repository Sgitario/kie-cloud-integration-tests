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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.kie.cloud.tests.context.wrappers.KieServer;
import org.kie.cloud.tests.utils.Deployments;
import org.kie.cloud.tests.utils.OpenshiftExtensionModelUtils;
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.KieServiceResponse.ResponseType;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.cloud.tests.utils.AwaitilityUtils.awaitsLong;

public interface KieServerSteps extends Steps {

    default void thenKieServersStartUpOk(String username, String password) {
        assertKieServersFor(username, password, (c, d) -> {
            ServiceResponse<KieServerInfo> info = c.getServerInfo();
            assertEquals(ResponseType.SUCCESS, info.getType());
            List<String> errors = OpenshiftExtensionModelUtils.getErrorMessagesFromServerInfo(info);
            assertTrue(errors.isEmpty(), () -> errors.stream().collect(Collectors.joining()));
        });
    }

    default void assertKieServersFor(String username, String password, BiConsumer<KieServicesClient, KieServer> action) {
        forEachKieServer(deployment -> {
            awaitsLong().until(() -> {
                try {
                    KieServicesClient client = deployment.restClient(username, password);
                    action.accept(client, deployment);
                } catch (RuntimeException ex) {
                    if (ex.getCause() instanceof KieServicesHttpException && ((KieServicesHttpException) ex.getCause()).getHttpCode() > 500) {
                        return false;
                    }

                    fail(String.format("User '%s:%s' cannot login in Kie Server. Cause: %s", username, password, ex.getMessage()));
                }

                return true;
            });
        });
    }

    default void forEachKieServer(Consumer<KieServer> action) {
        forEachDeployment(name -> name.endsWith(Deployments.KIE_SERVER), deployment -> action.accept(new KieServer(deployment)));
    }
}
