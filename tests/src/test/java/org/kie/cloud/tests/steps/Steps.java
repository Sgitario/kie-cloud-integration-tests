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

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.kie.cloud.tests.core.context.Deployment;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.environment.Environment;
import org.kie.cloud.tests.loader.Loader;

public interface Steps {

    Loader getCurrentLoader();

    TestContext getTestContext();

    Environment getEnvironment();

    String getUserName();

    String getUserPassword();

    default void forEachDeployment(Predicate<String> match, Consumer<Deployment> action) {
        getTestContext().getDeployments().entrySet().stream().filter(entry -> match.test(entry.getKey())).forEach(entry -> action.accept(entry.getValue()));
    }
}
