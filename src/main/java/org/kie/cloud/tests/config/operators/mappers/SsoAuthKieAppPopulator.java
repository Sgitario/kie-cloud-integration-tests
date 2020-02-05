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
package org.kie.cloud.tests.config.operators.mappers;

import java.util.Map;

import org.kie.cloud.tests.SingleSignOnBaseTest;
import org.kie.cloud.tests.config.operators.Auth;
import org.kie.cloud.tests.config.operators.KieApp;
import org.kie.cloud.tests.config.operators.Sso;
import org.springframework.stereotype.Component;

@Component
public class SsoAuthKieAppPopulator implements KieAppPopulator {

    @Override
    public void populate(KieApp app, Map<String, String> extraParams) {
        String ssoUrl = extraParams.get(SingleSignOnBaseTest.SSO_URL);
        if (ssoUrl == null) {
            return;
        }

        Sso sso = new Sso();
        sso.setUrl(ssoUrl);
        sso.setAdminUser(extraParams.get(SingleSignOnBaseTest.SSO_USERNAME));
        sso.setAdminPassword(extraParams.get(SingleSignOnBaseTest.SSO_PASSWORD));
        sso.setRealm(extraParams.get(SingleSignOnBaseTest.SSO_REALM));
        sso.setDisableSSLCertValidation(true);

        if (app.getSpec().getAuth() == null) {
            app.getSpec().setAuth(new Auth());
        }

        app.getSpec().getAuth().setSso(sso);
    }
}
