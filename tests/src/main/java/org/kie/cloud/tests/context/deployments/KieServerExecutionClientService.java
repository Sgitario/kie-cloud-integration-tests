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
package org.kie.cloud.tests.context.deployments;

import lombok.RequiredArgsConstructor;
import org.kie.cloud.tests.core.context.Deployment;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.core.deployments.post.PostLoadDeployment;
import org.kie.cloud.tests.core.properties.CredentialsProperties;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KieServerExecutionClientService implements PostLoadDeployment {

    private static final String URL = "http://insecure-%s-%s.project.openshiftdomain/services/rest/server";
    private static final String ROUTE_PARAM = "KIE_SERVER_ROUTE_NAME";

    private final CredentialsProperties credentials;
    private String username;
    private String password;
    private String url;
    private KieServicesClient client;

    public synchronized KieServicesClient client() {
        if (client == null) {
            client = KieServicesFactory.newKieServicesClient(KieServicesFactory.newRestConfiguration(url, username, password));
        }

        return client;
    }

    public KieServerExecutionClientService login() {
        return login(credentials.getUser(), credentials.getPassword());
    }

    public KieServerExecutionClientService login(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    @Override
    public void process(TestContext testContext, Deployment deployment) {
        String kieServerRoute = deployment.getEnvironmentVariable(ROUTE_PARAM);
        if (kieServerRoute != null) {
            this.url = String.format(URL, kieServerRoute, testContext.getProject().getName());
            this.login();
        }
    }
}
