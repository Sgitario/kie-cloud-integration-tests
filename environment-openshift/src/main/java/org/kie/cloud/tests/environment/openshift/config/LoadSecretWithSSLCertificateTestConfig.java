package org.kie.cloud.tests.environment.openshift.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.kie.cloud.tests.core.config.TestConfig;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.environment.openshift.OpenshiftEnvironmentImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoadSecretWithSSLCertificateTestConfig implements TestConfig {

    private static final String CLIENT_SUFFIX = "_client.ts";
    private static final String SERVER_SUFFIX = ".jks";

    @Value("${test.project.certificate.location}")
    private String certificatePath;

    @Value("${test.project.certificate.keypass}")
    private String keypass;

    @Value("${test.project.certificate.secret-name}")
    private String secretName;

    private final OpenshiftEnvironmentImpl openshift;

	@Override
    public void before(TestContext testContext) {
        try {
            openshift.createEncodedSecret(testContext.getProject(), secretName, Collections.singletonMap("keystore.jks", serverKeyStore()));
            testContext.setSecret(secretName);

            System.setProperty("javax.net.ssl.trustStore", clientCertificate());
            System.setProperty("javax.net.ssl.trustStorePassword", keypass);
        } catch (IOException e) {
            log.error("Loading SSL certificate", e);
            fail("Fail to load the SSL certificate");
        }
	}

    private String clientCertificate() {
        return Paths.get(certificatePath + CLIENT_SUFFIX).toAbsolutePath().toString();
    }

    private String serverKeyStore() throws IOException {
        return Base64.encodeBase64String(Files.readAllBytes(Paths.get(certificatePath + SERVER_SUFFIX)));
	}

}
