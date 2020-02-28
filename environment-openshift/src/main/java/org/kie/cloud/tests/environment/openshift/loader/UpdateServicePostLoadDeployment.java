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
package org.kie.cloud.tests.environment.openshift.loader;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.kie.cloud.tests.core.context.Deployment;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.core.deployments.post.PostLoadDeployment;
import org.kie.cloud.tests.environment.openshift.OpenshiftEnvironmentImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateServicePostLoadDeployment implements PostLoadDeployment {

    private final OpenshiftEnvironmentImpl openshift;

    @Override
    public void process(TestContext testContext, Deployment deployment) {
        Optional.ofNullable(openshift.getServiceByApplication(testContext.getProject(), deployment.getName())).ifPresent(service -> {
            deployment.setInternalIpAddress(service.getSpec().getClusterIP());
        });
    }
}
