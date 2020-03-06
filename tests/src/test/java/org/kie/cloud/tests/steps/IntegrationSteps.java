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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.kie.server.controller.api.model.spec.ServerTemplateList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface IntegrationSteps extends KieServerSteps, BusinessCentralSteps {

    default void thenKieServersAreConnectedWithBusinessCentrals() {
        assertKieServersFor((c, d) -> {

        });

        Set<String> expected = new HashSet<>();
        forEachKieServer(kieServer -> expected.add(kieServer.getAppName()));
        assertBusinessCentralsFor((c, d) -> {
            ServerTemplateList actual = c.listServerTemplates();
            assertNotNull(actual, "Error getting servers");
            assertNotNull(actual.getServerTemplates(), "No server instances returned");
            assertEquals(expected.size(), actual.getServerTemplates().length, "mismatch of kie server instances");
            expected.stream().forEach(kieServerExpected -> {
                assertTrue(Stream.of(actual.getServerTemplates()).anyMatch(serverTemplate -> serverTemplate.getId().equals(kieServerExpected)));
            });
        });
    }

}
