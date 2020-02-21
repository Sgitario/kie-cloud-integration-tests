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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.utils.AwaitilityUtils;
import org.kie.cloud.tests.utils.KieServerClientImpl;
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.server.client.KieServicesClient;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.KieServerControllerClientFactory;
import org.kie.server.controller.client.exception.KieServerControllerHTTPClientException;

import static org.junit.Assert.fail;

@Disabled
public class SsoTest {

    private static final String BC_URL = "http://insecure-myapp-rhpamcentr-josecarvajalhilario-local-5918.project.openshiftdomain/rest/controller";
    private static final String KIESERVER_URL = "http://insecure-myapp-kieserver-josecarvajalhilario-tests-4e67.project.openshiftdomain/services/rest/server";

    @Test
    public void bcUsingDefault() throws MalformedURLException, IOException {
        String username = "admin";
        String password = "admin";
        KieServerControllerClient responseCreateServerTemplate = KieServerControllerClientFactory.newRestClient(BC_URL, username, password);

        System.out.println(responseCreateServerTemplate.listServerTemplates());
    }

    @Test
    public void bcUsingSso() throws MalformedURLException, IOException {
        String username = "appUseraa";
        String password = "appUser1ss!";
        try (KieServerControllerClient responseCreateServerTemplate = KieServerControllerClientFactory.newRestClient(BC_URL, username, password)) {
            AwaitilityUtils.awaitsFast().until(() -> {
                try {
                    System.out.println(responseCreateServerTemplate.listServerTemplates());
                } catch (KieServerControllerHTTPClientException ex) {
                    if (ex.getResponseCode() > 500) { // perhaps the service is not ready yet
                        return false;
                    }

                    fail("Error in service");
                }

                return true;
            });

        }
    }

    @Test
    public void kieServerUsingApi() throws MalformedURLException, IOException {
        String username = "adminUser";
        String password = "adminUser1!";
        try {
            KieServicesClient responseCreateServerTemplate = new KieServerClientImpl(KIESERVER_URL, username, password);
            System.out.println(responseCreateServerTemplate.getServerInfo());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            if (ex.getCause() instanceof KieServicesHttpException) {
                KieServicesHttpException cause = (KieServicesHttpException) ex.getCause();

                fail("Cause: " + cause.getHttpCode());
            }

            fail("Error in service: " + ex.getCause());
        }

    }
}
