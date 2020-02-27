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

import java.util.Collections;
import java.util.Map;

import org.kie.cloud.tests.loader.TemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class PartyBaseTest extends BaseTest {

    @Autowired
    private TemplateLoader templateLoader;

    protected abstract String getParty();

    protected Map<String, String> getPartyExtraParams() {
        return Collections.emptyMap();
    }

    protected void onAfterDeploymentTemplate() {
        super.beforeOnLoadScenario();
    }

    @Override
    protected void beforeOnLoadScenario() {
        templateLoader.load(getTestContext(), getParty(), getPartyExtraParams());
        onAfterDeploymentTemplate();
    }
}
