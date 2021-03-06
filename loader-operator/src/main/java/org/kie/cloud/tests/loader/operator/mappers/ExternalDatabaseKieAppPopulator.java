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

import org.kie.cloud.tests.loader.operator.model.Build;
import org.kie.cloud.tests.loader.operator.model.Database;
import org.kie.cloud.tests.loader.operator.model.ExternalConfig;
import org.kie.cloud.tests.loader.operator.model.KieApp;
import org.springframework.stereotype.Component;

@Component
public class ExternalDatabaseKieAppPopulator extends KieAppPopulator {

    private static final String EXTENSIONS_IMAGE = "EXTENSIONS_IMAGE";
    private static final String EXTENSIONS_IMAGE_NAMESPACE = "EXTENSIONS_IMAGE_NAMESPACE";
    private static final String KIE_SERVER_EXTERNALDB_DRIVER = "KIE_SERVER_EXTERNALDB_DRIVER";
    private static final String KIE_SERVER_EXTERNALDB_DIALECT = "KIE_SERVER_EXTERNALDB_DIALECT";
    private static final String KIE_SERVER_EXTERNALDB_SERVICE_HOST = "KIE_SERVER_EXTERNALDB_SERVICE_HOST";
    private static final String KIE_SERVER_EXTERNALDB_SERVICE_PORT = "KIE_SERVER_EXTERNALDB_SERVICE_PORT";
    private static final String KIE_SERVER_EXTERNALDB_MIN_POOL_SIZE = "KIE_SERVER_EXTERNALDB_MIN_POOL_SIZE";
    private static final String KIE_SERVER_EXTERNALDB_MAX_POOL_SIZE = "KIE_SERVER_EXTERNALDB_MAX_POOL_SIZE";
    private static final String KIE_SERVER_EXTERNALDB_CONNECTION_CHECKER = "KIE_SERVER_EXTERNALDB_CONNECTION_CHECKER";
    private static final String KIE_SERVER_EXTERNALDB_EXCEPTION_SORTER = "KIE_SERVER_EXTERNALDB_EXCEPTION_SORTER";
    private static final String KIE_SERVER_EXTERNALDB_USER = "KIE_SERVER_EXTERNALDB_USER";
    private static final String KIE_SERVER_EXTERNALDB_PWD = "KIE_SERVER_EXTERNALDB_PWD";
    private static final String KIE_SERVER_EXTERNALDB_DB = "KIE_SERVER_EXTERNALDB_DB";

    @Override
    public void populate(KieApp app, Map<String, String> extraParams) {
        String extensionsImage = extraParams.get(EXTENSIONS_IMAGE);
        if (extensionsImage != null) {
            forEachServer(app, server -> {
                if (server.getBuild() == null) {
                    server.setBuild(new Build());
                }

                server.getBuild().setExtensionImageStreamTag(extensionsImage);
                server.getBuild().setExtensionImageStreamTagNamespace(extraParams.get(EXTENSIONS_IMAGE_NAMESPACE));

                server.setDatabase(createDatabase(extraParams));
            });
        }
    }

    private Database createDatabase(Map<String, String> extraParams) {
        Database database = new Database();
        database.setType("external");
        database.setExternalConfig(createExternalConfig(extraParams));

        return database;
    }

    private ExternalConfig createExternalConfig(Map<String, String> extraParams) {
        ExternalConfig config = new ExternalConfig();
        config.setDriver(extraParams.get(KIE_SERVER_EXTERNALDB_DRIVER));
        config.setDialect(extraParams.get(KIE_SERVER_EXTERNALDB_DIALECT));
        config.setHost(extraParams.get(KIE_SERVER_EXTERNALDB_SERVICE_HOST));
        config.setPort(extraParams.get(KIE_SERVER_EXTERNALDB_SERVICE_PORT));
        config.setMinPoolSize(extraParams.get(KIE_SERVER_EXTERNALDB_MIN_POOL_SIZE));
        config.setMaxPoolSize(extraParams.get(KIE_SERVER_EXTERNALDB_MAX_POOL_SIZE));
        config.setConnectionChecker(extraParams.get(KIE_SERVER_EXTERNALDB_CONNECTION_CHECKER));
        config.setExceptionSorter(extraParams.get(KIE_SERVER_EXTERNALDB_EXCEPTION_SORTER));
        config.setUsername(extraParams.get(KIE_SERVER_EXTERNALDB_USER));
        config.setPassword(extraParams.get(KIE_SERVER_EXTERNALDB_PWD));
        config.setName(extraParams.get(KIE_SERVER_EXTERNALDB_DB));
        return config;
    }

}
