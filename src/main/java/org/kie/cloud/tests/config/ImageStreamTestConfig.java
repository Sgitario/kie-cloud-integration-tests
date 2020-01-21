package org.kie.cloud.tests.config;

import java.io.IOException;

import org.kie.cloud.tests.clients.openshift.OpenshiftClient;
import org.kie.cloud.tests.context.TestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ImageStreamTestConfig implements TestConfig {

	@Value("${test.common.config.image.streams}")
	private Resource imageStreamFile;

	@Autowired
	private OpenshiftClient openshift;

	@Override
	public void before(TestContext testContext) {
		try {
			openshift.loadResources(testContext.getProject(), imageStreamFile.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
