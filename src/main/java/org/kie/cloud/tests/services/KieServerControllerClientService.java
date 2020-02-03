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
package org.kie.cloud.tests.services;

import lombok.RequiredArgsConstructor;
import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.properties.CredentialsProperties;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.KieServerControllerClientFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KieServerControllerClientService implements PostLoadDeploymentService {

    private static final String URL = "http://insecure-%s-%s.project.openshiftdomain/rest/controller";
    private static final String ROUTE_PARAM = "WORKBENCH_ROUTE_NAME";

    private final CredentialsProperties credentials;

    private KieServerControllerClient client;

    public KieServerControllerClient getClient() {
        return client;
    }

    @Override
    public void process(TestContext testContext, Deployment deployment) {
        String kieServerRoute = deployment.getEnvironmentVariable(ROUTE_PARAM);
        if (kieServerRoute != null) {
            client = KieServerControllerClientFactory.newRestClient(url(testContext, kieServerRoute), credentials.getUser(), credentials.getPassword());
        }
    }

    private String url(TestContext testContext, String kieServerRoute) {
        return String.format(URL, kieServerRoute, testContext.getProject().getName());
    }
}
