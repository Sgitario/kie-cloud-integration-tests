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
package org.kie.cloud.tests.actions;

import org.kie.cloud.tests.config.templates.ActionDefinition;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.services.ExpressionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseAction implements Action {

    @Autowired
    private ExpressionEvaluator evaluator;

    public String getParam(ActionDefinition action, String key, TestContext testContext) {
        return evaluator.resolveValue(key, action.getParam(key), testContext);
    }

}
