package org.kie.cloud.tests.config.templates;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.openshift.api.model.Template;
import lombok.Getter;

@Getter
public final class TemplateInstance {

	private final Template templateResult;
	private final List<Deployment> deployments;

	public TemplateInstance(Template templateResult, List<Deployment> deployments) {
		this.templateResult = templateResult;
		this.deployments = deployments;
	}

	public Map<String, String> getAllDeploymentProperties() {
		Map<String, String> map = new HashMap<>();
		deployments.forEach(deployment -> map.putAll(deployment.getEnvironmentVariables()));
		return Collections.unmodifiableMap(map);
	}

}
