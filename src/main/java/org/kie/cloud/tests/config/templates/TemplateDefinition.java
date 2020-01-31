package org.kie.cloud.tests.config.templates;

import java.util.Map;

import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class TemplateDefinition {
	private Resource file;
    private Map<String, String> params;
	private Resource customImageStreamsFile;
	private Resource customSecretsFile;
}
