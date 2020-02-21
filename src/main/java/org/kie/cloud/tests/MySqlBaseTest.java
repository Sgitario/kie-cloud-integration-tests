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
package org.kie.cloud.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.kie.cloud.tests.utils.Parties;

@Tag("mysql")
public abstract class MySqlBaseTest extends PartyBaseTest {

    @Override
    protected String getParty() {
        return Parties.MYSQL;
    }

    @Override
    protected Map<String, String> scenarioExtraParams() {
        Map<String, String> params = new HashMap<>();
        params.putAll(super.scenarioExtraParams());
        params.putAll(getDeploymentParams(Parties.MYSQL));
        return params;
    }
}
