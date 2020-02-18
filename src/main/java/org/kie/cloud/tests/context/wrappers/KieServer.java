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
package org.kie.cloud.tests.context.wrappers;

import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.utils.KieServerClientImpl;
import org.kie.server.client.KieServicesClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class KieServer {

    private static final String API_PATH = "/services/rest/server";

    private final Deployment deployment;

    public KieServer(Deployment deployment) {
        this.deployment = deployment;
    }

    public KieServicesClient restClient(String username, String password) {
        if (deployment.getChannel() == null) {
            assertNotNull(deployment.getHttpUrl(), "Http URL is null!");
            deployment.setChannel(new KieServerClientImpl(deployment.getHttpUrl() + API_PATH, username, password));
        }

        return (KieServicesClient) deployment.getChannel();
    }

    public String getInternalIpAddress() {
        return deployment.getInternalIpAddress();
    }

    public String getAppName() {
        return deployment.getName();
    }
}
