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
package org.kie.cloud.tests.utils;

import java.util.Collection;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public final class YamlUtils {

    private static YamlUtils INSTANCE;

    private final Yaml yaml;

    private YamlUtils() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        this.yaml = new Yaml(CUSTOM_REPRESENTER, options);
    }

    public static synchronized final YamlUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new YamlUtils();
        }

        return INSTANCE;
    }

    public String toYaml(Object obj) {
        return yaml.dump(obj);
    }

    private static final Representer CUSTOM_REPRESENTER = new Representer() {

        @SuppressWarnings("rawtypes")
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            // if value of property is null, ignore it.
            if (propertyValue == null) {
                return null;
            } else if (propertyValue instanceof Collection && ((Collection) propertyValue).isEmpty()) {
                return null;
            } else if (propertyValue instanceof Map && ((Map) propertyValue).isEmpty()) {
                return null;
            } else if (Arrays.isArray(propertyValue) && Arrays.isNullOrEmpty((Object[]) propertyValue)) {
                return null;
            } else {
                return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        }
    };

}
