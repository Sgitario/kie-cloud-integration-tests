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
package org.kie.cloud.tests.config;

import lombok.RequiredArgsConstructor;
import org.kie.cloud.tests.core.config.TestConfig;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.properties.TestCommonProperties;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PropertiesTestConfig implements TestConfig {

    private final TestCommonProperties common;

    @Override
    public void before(TestContext testContext) {
        testContext.setProperties(common.getProperties());
    }

}
