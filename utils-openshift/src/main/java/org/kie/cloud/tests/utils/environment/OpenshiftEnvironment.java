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
package org.kie.cloud.tests.utils.environment;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.kie.cloud.tests.core.context.Deployment;
import org.kie.cloud.tests.core.context.Project;

public interface OpenshiftEnvironment {

    /**
     * Routes
     */
    List<String> getRouteByApplication(Project project, String deploymentName);

    void createRouteForService(Project project, String name, String targetHost, String deploymentName);

    /**
     * Custom Resource Definitions
     */
    void createGlobalCustomResourceDefinition(String customResourceDefinition, InputStream inputStream);

    /**
     * Resources
     */
    void createResource(Project project, InputStream inputStream);

    /**
     * Deployments
     */
    Deployment getDeployment(Project project, String deploymentName);

    /**
     * Templates
     */
    List<Deployment> createTemplate(Project project, InputStream content, Map<String, String> parameters);

    /**
     * Get latest image stream URL
     */
    String getLatestImageStreamUrl(Project project, String imageName);

    /**
     * Get image stream URL by version
     */
    String getImageStreamUrlByVersion(Project project, String imageName, String imageStreamVersion);

    /**
     * Wait for rollout a deployment.
     */
    void waitForRollout(Project project, Deployment deployment);

}
