package org.kie.cloud.tests.context;

import java.util.HashMap;
import java.util.Map;

import org.kie.cloud.tests.clients.openshift.Project;

import lombok.Data;

@Data
public class TestContext {
	private final Project project;
	private String secret;
	private final Map<String, String> params = new HashMap<>();

	public void addParam(String key, String value) {
		params.put(key, value);
	}
}
