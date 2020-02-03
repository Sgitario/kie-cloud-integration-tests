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

import org.kie.cloud.tests.services.KieServerControllerClientService;
import org.kie.cloud.tests.services.KieServerExecutionClientService;

public interface LoginSteps extends Steps {

    KieServerControllerClientService getKieServerControllerClientService();

    KieServerExecutionClientService getKieServerExecutionClientService();

    public default void thenCanLoginInKieServerControllerUsingDefaultUser() {
        tryAssert(() -> getKieServerControllerClientService().client().listServerTemplates(), "cannot login");
    }

    public default void thenCanLoginInKieServerControllerUsing(String username, String password) {
        tryAssert(() -> getKieServerControllerClientService().login(username, password).client().listServerTemplates(), "cannot login");
    }

    public default void thenCanLoginInKieServerExecutionUsingDefaultUser() {
        tryAssert(() -> getKieServerExecutionClientService().client().listContainers(), "cannot login");
    }

    public default void thenCanLoginInKieServerExecutionUsing(String username, String password) {
        tryAssert(() -> getKieServerExecutionClientService().login(username, password).client().listContainers(), "cannot login");
    }


}
