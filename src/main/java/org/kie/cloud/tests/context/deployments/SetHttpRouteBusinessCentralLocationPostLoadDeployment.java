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
import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.context.Deployment;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SetHttpRouteBusinessCentralLocationPostLoadDeployment implements PostLoadDeployment {

    private static final String INSECURE_PROTOCOL = "http:";

    private final OpenshiftClient openshift;

    @Override
    public void process(TestContext testContext, Deployment deployment) {
        openshift.getRouteByApplication(testContext.getProject(), deployment.getName()).stream()
                 .filter(url -> url.startsWith(INSECURE_PROTOCOL))
                 .findFirst().ifPresent(deployment::setHttpUrl);
    }
}
