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
package org.kie.cloud.tests.config.operators.mappers;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.kie.cloud.tests.config.operators.KieApp;
import org.kie.cloud.tests.config.operators.Objects;
import org.kie.cloud.tests.config.operators.Server;

public abstract class KieAppPopulator {

    public abstract void populate(KieApp app, Map<String, String> extraParams);

    protected void forEachServer(KieApp app, Consumer<Server> kieServerAction) {
        Objects objects = app.getSpec().getObjects();
        if (objects == null) {
            objects = new Objects();
            app.getSpec().setObjects(objects);
        }

        if (objects.getServers() == null || objects.getServers().length == 0) {
            objects.addServer(new Server());
        }

        Stream.of(objects.getServers()).forEach(kieServerAction);
    }

}
