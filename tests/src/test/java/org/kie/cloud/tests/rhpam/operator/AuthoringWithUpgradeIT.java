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
package org.kie.cloud.tests.rhpam.operator;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.JbpmBaseTest;
import org.kie.cloud.tests.core.constants.UpgradeConstants;
import org.kie.cloud.tests.core.constants.VersionConstants;
import org.kie.cloud.tests.loader.operator.OperatorLoader;
import org.kie.cloud.tests.loader.templates.DisabledOnTemplates;
import org.kie.cloud.tests.steps.IntegrationSteps;
import org.kie.cloud.tests.steps.LoginSteps;
import org.kie.cloud.tests.utils.Scenarios;
import org.kie.cloud.tests.utils.environment.OpenshiftEnvironment;
import org.springframework.beans.factory.annotation.Autowired;

@DisabledOnTemplates
@DisplayName("RHPAM Authoring with Upgrades")
public class AuthoringWithUpgradeIT extends JbpmBaseTest implements LoginSteps, IntegrationSteps {

    private static final String FROM_VERSION = "7.6.0";
    private static final String TO_VERSION = "7.7.0";

    @Autowired
    private OpenshiftEnvironment environment;

    @Autowired
    private OperatorLoader operatorLoader;

    @Test
    @Tag("upgrades")
    public void shouldUpgradeToLatestVersion() {
        whenUpgradeTheOperator();
        thenAllDeploymentsAreUpdated();
    }

    @Override
    protected String scenario() {
        return Scenarios.RHPAM_AUTHORING;
    }

    @Override
    protected Map<String, String> scenarioExtraParams() {
        Map<String, String> params = new HashMap<>();
        params.putAll(super.scenarioExtraParams());
        params.put(UpgradeConstants.ENABLE_UPGRADE, UpgradeConstants.MINOR);
        params.put(VersionConstants.OVERRIDES_VERSION, FROM_VERSION);
        return params;
    }

    private void whenUpgradeTheOperator() {
        operatorLoader.changeOperatorVersion(getTestContext(), TO_VERSION);
    }

    private void thenAllDeploymentsAreUpdated() {
        forEachBusinessCentral(bc -> environment.waitForRollout(getTestContext().getProject(), bc.getDeployment()));
        forEachKieServer(kie -> environment.waitForRollout(getTestContext().getProject(), kie.getDeployment()));
    }
}
