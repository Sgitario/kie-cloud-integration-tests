package org.kie.cloud.tests.environment.openshift.config;

import java.io.IOException;
import java.io.InputStream;

import lombok.RequiredArgsConstructor;
import org.kie.cloud.tests.core.config.TestConfig;
import org.kie.cloud.tests.core.context.Mode;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.environment.openshift.OpenshiftEnvironmentImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageStreamTestConfig implements TestConfig {

    @Value("${test.common.config.jbpm.image.streams}")
    private Resource jbpmImageStream;

    @Value("${test.common.config.drools.image.streams}")
    private Resource droolsImageStream;

    private final OpenshiftEnvironmentImpl openshift;

	@Override
	public void before(TestContext testContext) {
		try {
            openshift.createResource(testContext.getProject(), resourceFromMode(testContext));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    private InputStream resourceFromMode(TestContext testContext) throws IOException {
        Resource resource = jbpmImageStream;
        if (testContext.getMode() == Mode.DROOLS) {
            resource = droolsImageStream;
        }

        return resource.getInputStream();
    }

}
