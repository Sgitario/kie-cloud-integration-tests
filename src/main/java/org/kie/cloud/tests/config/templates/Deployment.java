package org.kie.cloud.tests.config.templates;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Deployment {
	private final String name;
	private final Map<String, String> environmentVariables;
}
