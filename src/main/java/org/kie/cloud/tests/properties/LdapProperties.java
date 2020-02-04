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
package org.kie.cloud.tests.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("ldap")
public class LdapProperties {

    private String url;
    private String user;
    private String password;
    private String bindDn;
    private String bindCredential;
    private String baseCtxDn;
    private String baseFilter;
    private String searchScope;
    private String searchTimeLimit;
    private String roleAttributeId;
    private String rolesCtxDn;
    private String roleFilter;
    private String roleRecursion;
    private String defaultRole;
}
