package org.kie.cloud.tests.environment.openshift.config;

import java.io.IOException;
import java.io.InputStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.cloud.tests.core.config.TestConfig;
import org.kie.cloud.tests.core.context.TestContext;
import org.kie.cloud.tests.utils.environment.OpenshiftEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageStreamTestConfig implements TestConfig {

    private static final String MODE_REPLACE = "{mode}";

    @Value("${test.repo.path.bxms-qe-tests}")
    private String repoLocation;

    @Value("${test.common.config.image.streams.format}")
    private String imageLocationPath;

    private final OpenshiftEnvironment openshift;
    private final ResourceLoader resourceLoader;

	@Override
	public void before(TestContext testContext) {
        openshift.createResource(testContext.getProject(), resourceFromMode(testContext));
	}

    private InputStream resourceFromMode(TestContext testContext) {
        try {
            String imageStreamLocation = repoLocation + imageLocationPath.replace(MODE_REPLACE, testContext.getMode().name().toLowerCase());
            log.info("Loading images from ... {}", imageStreamLocation);
            return resourceLoader.getResource(imageStreamLocation).getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Error loading image stream", e);
        }
    }

}
