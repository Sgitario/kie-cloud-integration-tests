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
package org.kie.cloud.tests.loader.operator.mappers;

import java.util.Map;

import org.kie.cloud.tests.core.constants.UpgradeConstants;
import org.kie.cloud.tests.loader.operator.model.KieApp;
import org.kie.cloud.tests.loader.operator.model.Upgrades;
import org.springframework.stereotype.Component;

@Component
public class EnableUpgradesMinorKieAppPopulator extends KieAppPopulator {

    @Override
    public void populate(KieApp app, Map<String, String> extraParams) {
        String enabledOption = extraParams.get(UpgradeConstants.ENABLE_UPGRADE);
        if (enabledOption != null) {
            Upgrades upgrades = new Upgrades();
            upgrades.setEnabled(true);
            upgrades.setMinor(UpgradeConstants.MINOR.equals(enabledOption));
            app.getSpec().setUpgrades(upgrades);
        }
    }

}
