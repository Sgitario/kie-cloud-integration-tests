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
package org.kie.cloud.tests.support;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.KieServerControllerClientFactory;

@Ignore
public class SsoTest {

    private static final String BC_URL = "http://insecure-myapp-rhpamcentr-jch-tests-496e.project.openshiftdomain/rest/controller";
    private static final String KIESERVER_URL = "http://insecure-myapp-kieserver-jch-tests-d39d.project.openshiftdomain/services/rest/server";

    @Test
    public void bcUsingDefault() throws MalformedURLException, IOException {
        String username = "adminUser";
        String password = "adminUser1!";
        KieServerControllerClient responseCreateServerTemplate = KieServerControllerClientFactory.newRestClient(BC_URL, username, password);

        System.out.println(responseCreateServerTemplate.listServerTemplates());
    }

    @Test
    public void bcUsingSso() throws MalformedURLException, IOException {
        String username = "serviceUser";
        String password = "serviceUser1!";
        KieServerControllerClient responseCreateServerTemplate = KieServerControllerClientFactory.newRestClient(BC_URL, username, password);

        System.out.println(responseCreateServerTemplate.listServerTemplates());
    }

    @Test
    public void kieServerUsingApi() throws MalformedURLException, IOException {
        String username = "adminUser";
        String password = "adminUser1!";
        KieServicesClient responseCreateServerTemplate = KieServicesFactory.newKieServicesClient(KieServicesFactory.newRestConfiguration(KIESERVER_URL, username, password));

        System.out.println(responseCreateServerTemplate.listContainers());
    }
}
