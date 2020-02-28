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
package org.kie.cloud.tests.loader.operator.environment;

import cz.xtf.core.openshift.OpenShifts;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kie.cloud.tests.core.context.Project;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.loader.operator.model.KieApp;
import org.kie.cloud.tests.loader.operator.model.KieAppDoneable;
import org.kie.cloud.tests.loader.operator.model.KieAppList;
import org.kie.cloud.tests.utils.YamlUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OperatorOpenshiftLoader {

    public KieApp loadOperator(TestContext testContext, KieApp app) {
        if (log.isDebugEnabled()) {
            log.debug("Loading Kie App: ");
            log.debug(YamlUtils.getInstance().toYaml(app));
        }

        return operatorClient(testContext.getProject()).create(app);
    }

    private NonNamespaceOperation<KieApp, KieAppList, KieAppDoneable, Resource<KieApp, KieAppDoneable>> operatorClient(Project project) {
        CustomResourceDefinition customResourceDefinition = OpenShifts.admin().customResourceDefinitions().withName("kieapps.app.kiegroup.org").get();
        return OpenShifts.admin().customResources(customResourceDefinition, KieApp.class, KieAppList.class, KieAppDoneable.class).inNamespace(project.getName());
    }
}
