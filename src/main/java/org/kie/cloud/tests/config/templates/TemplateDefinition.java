package org.kie.cloud.tests.config.templates;

import java.util.Map;

import org.springframework.core.io.Resource;

import lombok.Data;

@Data
public class TemplateDefinition {
	private String name;
	private Resource file;
	private Map<String, String> params;
	private Resource customImageStreamsFile;
	private Resource customSecretsFile;
}
