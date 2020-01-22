package org.kie.cloud.tests.context;

import java.beans.Transient;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Deployment {
	private final String name;
	private final Map<String, String> environmentVariables;

	@Transient
	public String getEnvironmentVariable(String paramName) {
		return environmentVariables.get(paramName);
	}
}
