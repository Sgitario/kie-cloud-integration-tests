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
package org.kie.cloud.tests.app;

import org.kie.cloud.tests.Base;
import org.kie.cloud.tests.core.context.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "org.kie.cloud")
@EnableConfigurationProperties
public class TestScriptRunner extends Base implements CommandLineRunner {

    @Autowired
    private ScenarioProperties scenario;

    public static void main(String[] args) {
        SpringApplication.run(TestScriptRunner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            startUp(Mode.JBPM);
            whenLoadScenario(scenario.getName(), scenario.getParams());
        } finally {
            cleanUp();
        }
    }
}
