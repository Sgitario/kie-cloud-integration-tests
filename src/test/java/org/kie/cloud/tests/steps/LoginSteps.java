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

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.cloud.tests.utils.AssertUtils.tryAssert;

public interface LoginSteps extends Steps {

    public default void thenCanLoginInBusinessCentral(String username, String password) {
        forEachBusinessCentral(deployment -> tryAssert(() -> deployment.restClient(username, password).listServerTemplates(), "cannot login business central"));
    }

    public default void thenCannotLoginInBusinessCentral(String username, String password) {
        try {
            thenCanLoginInBusinessCentral(username, password);
        } catch (AssertionError error) {
            assertTrue(true);
            return;
        }

        fail("It could login.");
    }

    public default void thenCanLoginInKieServer(String username, String password) {
        forEachKieServer(deployment -> tryAssert(() -> deployment.restClient(username, password).listContainers(), "cannot login kie server"));
    }

    public default void thenCannotLoginInKieServer(String username, String password) {
        try {
            thenCanLoginInBusinessCentral(username, password);
        } catch (AssertionError error) {
            assertTrue(true);
            return;
        }

        fail("It could login.");
    }

}
