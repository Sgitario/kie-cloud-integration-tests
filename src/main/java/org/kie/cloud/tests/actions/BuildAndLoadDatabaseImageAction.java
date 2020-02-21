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

import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;
import org.kie.cloud.tests.config.templates.ActionDefinition;
import org.kie.cloud.tests.context.TestContext;
import org.kie.cloud.tests.utils.LocationConstants;
import org.kie.cloud.tests.utils.ProcessExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BuildAndLoadDatabaseImageAction extends BaseAction {

    private static final String BUILD_NAME = "BUILD_NAME";
    private static final String SOURCE_DOCKER_TAG = "SOURCE_DOCKER_TAG";
    private static final String TARGET_DOCKER_TAG = "TARGET_DOCKER_TAG";

    @Override
    public String name() {
        return "build-jdbc-image";
    }

    @Override
    public void run(ActionDefinition action, TestContext testContext) {
        String buildCommand = "make build " + action.getParam(BUILD_NAME);
        String sourceDockerTag = getParam(action, SOURCE_DOCKER_TAG, testContext);
        String targetDockerTag = getParam(action, TARGET_DOCKER_TAG, testContext);

        try (ProcessExecutor processExecutor = new ProcessExecutor()) {
            log.info("Building JDBC driver image.");
            processExecutor.executeProcessCommand(buildCommand, Paths.get(LocationConstants.JDBC_IMAGE_BUILD_FOLDER));

            log.info("Pushing JDBC driver image to Docker registry.");
            processExecutor.executeProcessCommand("docker tag " + sourceDockerTag + " " + targetDockerTag);
            processExecutor.executeProcessCommand("docker push " + targetDockerTag);
        }
    }

}
