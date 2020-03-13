package org.kie.cloud.tests.core.context;

import java.beans.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Data;

import static org.junit.jupiter.api.Assertions.fail;

@Data
public class TestContext {
	private final Project project;
    private final Mode mode;

	private String secret;
	private Map<String, Deployment> deployments = new HashMap<>();
    private Map<String, String> properties;

	@Transient
	public void putDeployment(Deployment deployment) {
		deployments.put(deployment.getName(), deployment);
	}

	@Transient
	public Deployment getDeployment(String deploymentName) {
		return Optional.ofNullable(deployments.get(deploymentName)).orElseGet(() -> {
			fail("Deployment " + deploymentName + " not found!");
			return null;
		});
	}

    @Transient
    public String getProperty(String key) {
        return properties.get(key);
    }
}
