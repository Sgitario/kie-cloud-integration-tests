package org.kie.cloud.tests.context;

import static org.junit.jupiter.api.Assertions.fail;

import java.beans.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.kie.cloud.tests.clients.openshift.Project;

import lombok.Data;

@Data
public class TestContext {
	private final Project project;
	private String secret;
	private Map<String, Deployment> deployments = new HashMap<>();

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
}
