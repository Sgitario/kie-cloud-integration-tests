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
package org.kie.cloud.tests.loader.templates.actions;

import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.core.services.actions.Action;
import org.kie.cloud.tests.core.services.actions.ActionDefinition;
import org.kie.cloud.tests.loader.templates.TemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class DeployTemplateAction implements Action {

    private static final String TEMPLATE = "TEMPLATE";

    @Lazy
    @Autowired
    private TemplateLoader templateLoader;

    @Override
    public String name() {
        return "deploy-template";
    }

    @Override
    public void run(ActionDefinition action, TestContext testContext) {
        templateLoader.load(testContext, action.getParam(TEMPLATE), action.getParams());
    }

}
