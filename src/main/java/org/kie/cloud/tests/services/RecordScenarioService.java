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
package org.kie.cloud.tests.services;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.cloud.tests.app.ScenarioProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecordScenarioService {

    private static final String TEST_SCRIPT_LOCATION = "target/test-script-%s.properties";
    private static final List<String> RECORD_PROPERTIES = Arrays.asList("test", "ldap", "xtf", "overrides", "client");

    private final ConfigurableEnvironment environment;

    @Value("${test.record:true}")
    private boolean enabled;

    public void record(String testName, String scenario, Map<String, String> scenarioParams) {
        if (!enabled) {
            return;
        }

        Properties properties = initProperties();
        loadScenario(properties, scenario, scenarioParams);
        loadProperties(properties);
        saveProperties(properties, testName);
    }

    private void loadScenario(Properties properties, String scenario, Map<String, String> scenarioParams) {
        properties.setProperty(ScenarioProperties.PREFIX + ".name", scenario);
        scenarioParams.forEach((k, v) -> {
            properties.setProperty(ScenarioProperties.PREFIX + ".params." + k, v);
        });
    }

    private void saveProperties(Properties properties, String testName) {
        try (OutputStream output = new FileOutputStream(String.format(TEST_SCRIPT_LOCATION, testName))) {
            properties.store(output, null);
        } catch (Exception e) {
            log.warn("Exception recording scenario. Test script won't be generated", e);
        }
    }

    @SuppressWarnings("serial")
    private Properties initProperties() {
        return new Properties() {

            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }
        };
    }

    @SuppressWarnings("rawtypes")
    private void loadProperties(Properties properties) {

        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
                    Object value = propertySource.getProperty(key);
                    if (isKeyRecordable(key) && value instanceof String) {
                        properties.put(key, value);
                    }
                }
            }
        }
    }

    private boolean isKeyRecordable(String key) {
        return RECORD_PROPERTIES.stream().anyMatch(key::startsWith);
    }
}
