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
import org.kie.cloud.tests.clients.sso.SsoClient;

@Disabled
public class SsoTest {

    private static final String SSO_URL = "https://secure-sso-josecarvajalhilario-tests-286b.apps.playground.rhba.openshift-aws.rhocf-dev.com/auth";

    @Test
    public void creatingClient() throws MalformedURLException, IOException {
        SsoClient.get(SSO_URL, "demo").createClient("ddd");
    }
}
