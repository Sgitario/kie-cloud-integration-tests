package org.kie.cloud.tests.environment.openshift.config;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.kie.cloud.tests.core.config.TestConfig;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.environment.openshift.OpenshiftEnvironmentImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageStreamTestConfig implements TestConfig {

	@Value("${test.common.config.image.streams}")
	private Resource imageStreamFile;

    private final OpenshiftEnvironmentImpl openshift;

	@Override
	public void before(TestContext testContext) {
		try {
            openshift.createResource(testContext.getProject(), imageStreamFile.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
