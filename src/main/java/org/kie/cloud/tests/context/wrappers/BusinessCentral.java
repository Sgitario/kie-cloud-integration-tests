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

import org.apache.commons.codec.binary.StringUtils;
import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.utils.CloseableUtils;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.KieServerControllerClientFactory;

public class BusinessCentral {

    private static final String REST_API_PATH = "/rest/controller";

    private final Deployment deployment;

    private String currentUsername;
    private String currentPassword;

    public BusinessCentral(Deployment deployment) {
        this.deployment = deployment;
    }

    public KieServerControllerClient restClient(String username, String password) {
        if (deployment.getChannel() != null && StringUtils.equals(username, currentUsername) && StringUtils.equals(password, currentPassword)) {
            return (KieServerControllerClient) deployment.getChannel();
        }

        if (deployment.getChannel() != null) {
            CloseableUtils.closeQuietly((Closeable) deployment.getChannel());
        }

        deployment.setChannel(KieServerControllerClientFactory.newRestClient(deployment.getInsecureUrl() + REST_API_PATH, username, password));
        return (KieServerControllerClient) deployment.getChannel();
    }
}
