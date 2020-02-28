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
package org.kie.cloud.tests.loader.operator.mappers;

import java.util.Map;

import org.kie.cloud.tests.core.constants.SsoContants;
import org.kie.cloud.tests.loader.operator.model.Auth;
import org.kie.cloud.tests.loader.operator.model.KieApp;
import org.kie.cloud.tests.loader.operator.model.Sso;
import org.kie.cloud.tests.loader.operator.model.SsoClient;
import org.springframework.stereotype.Component;

@Component
public class SsoAuthKieAppPopulator extends KieAppPopulator {

    @Override
    public void populate(KieApp app, Map<String, String> extraParams) {
        String ssoUrl = extraParams.get(SsoContants.SSO_URL);
        if (ssoUrl == null) {
            return;
        }

        Sso sso = new Sso();
        sso.setUrl(ssoUrl);
        sso.setAdminUser(extraParams.get(SsoContants.SSO_USERNAME));
        sso.setAdminPassword(extraParams.get(SsoContants.SSO_PASSWORD));
        sso.setRealm(extraParams.get(SsoContants.SSO_REALM));
        sso.setDisableSSLCertValidation(true);

        if (app.getSpec().getAuth() == null) {
            app.getSpec().setAuth(new Auth());
        }

        app.getSpec().getAuth().setSso(sso);

        setClients(app, extraParams);
    }

    private void setClients(KieApp app, Map<String, String> extraParams) {
        SsoClient ssoClient = new SsoClient();
        ssoClient.setName(extraParams.get(SsoContants.SSO_BUSINESS_CENTRAL_SSO_CLIENT));
        ssoClient.setSecret(extraParams.get(SsoContants.SSO_BUSINESS_CENTRAL_SSO_SECRET));
        app.getSpec().getObjects().getConsole().setSsoClient(ssoClient);

        forEachServer(app, server -> {
            SsoClient ssoKieServerClient = new SsoClient();
            ssoKieServerClient.setName(extraParams.get(SsoContants.SSO_KIE_SERVER_SSO_CLIENT));
            ssoKieServerClient.setSecret(extraParams.get(SsoContants.SSO_KIE_SERVER_SSO_SECRET));
            server.setSsoClient(ssoKieServerClient);
        });
    }
}
