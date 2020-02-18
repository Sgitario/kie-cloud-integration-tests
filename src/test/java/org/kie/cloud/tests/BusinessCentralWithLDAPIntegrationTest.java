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
package org.kie.cloud.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.cloud.tests.annotations.DisabledOnOperators;
import org.kie.cloud.tests.steps.LoginSteps;

@DisabledOnOperators
@DisplayName("Business Central with LDAP")
public class BusinessCentralWithLDAPIntegrationTest extends LdapBaseTest implements LoginSteps {

    @Override
    protected String scenario() {
        return "business-central";
    }

    @Tag("login")
    @Test
    public void shouldCanLogin() {
        thenCanLoginInBusinessCentral(getLdapUsername(), getLdapPassword());
    }

}
