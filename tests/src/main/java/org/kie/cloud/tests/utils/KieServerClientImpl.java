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

import java.util.ArrayList;
import java.util.List;

import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.impl.KieServicesClientImpl;

import static org.junit.Assert.fail;

public class KieServerClientImpl extends KieServicesClientImpl {

    public KieServerClientImpl(String url, String username, String password) {
        super(KieServicesFactory.newRestConfiguration(url, username, password));
    }

    @Override
    protected <T> ServiceResponse<T> makeHttpGetRequestAndCreateServiceResponse(String uri, Class<T> resultType) {
        List<ServiceResponse<T>> responses = new ArrayList<>();
        AwaitilityUtils.awaitsFast().until(() -> {
            try {
                responses.add(super.makeHttpGetRequestAndCreateServiceResponse(uri, resultType));
            } catch (KieServicesHttpException ex) {
                if (ex.getHttpCode() > 500) {
                    // perhaps the service is not ready yet
                    return false;
                }

                fail("Cause: " + ex.getHttpCode());
            }

            return true;
        });

        return responses.get(0);
    }

}