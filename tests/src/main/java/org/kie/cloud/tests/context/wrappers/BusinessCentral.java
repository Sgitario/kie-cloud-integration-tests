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

import java.io.Closeable;

import org.kie.cloud.tests.core.context.Deployment;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.environment.Environment;
import org.kie.cloud.tests.utils.CloseableUtils;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.KieServerControllerClientFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BusinessCentral {

    private static final String REST_API_PATH = "/rest/controller";

    private final TestContext testContext;
    private final Deployment deployment;
    private final Environment environment;

    public BusinessCentral(TestContext testContext, Deployment deployment, Environment environment) {
        this.testContext = testContext;
        this.deployment = deployment;
        this.environment = environment;
    }

    public KieServerControllerClient restClient(String username, String password) {
        if (deployment.getChannel() != null) {
            return (KieServerControllerClient) deployment.getChannel();
        }

        if (deployment.getChannel() != null) {
            CloseableUtils.closeQuietly((Closeable) deployment.getChannel());
        }

        assertNotNull(deployment.getHttpUrl(), "Http URL is null!");
        deployment.setChannel(KieServerControllerClientFactory.newRestClient(deployment.getHttpUrl() + REST_API_PATH, username, password));
        return (KieServerControllerClient) deployment.getChannel();
    }

    public void restart() {
        environment.restartDeployment(testContext.getProject(), deployment);
    }
}
