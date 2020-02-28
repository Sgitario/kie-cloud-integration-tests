package org.kie.cloud.tests.loader.templates;

import java.util.List;
import java.util.Map;

import lombok.Data;
import org.kie.cloud.tests.core.services.actions.ActionDefinition;
import org.springframework.core.io.Resource;

@Data
public class TemplateDefinition {
	private Resource file;
    private Map<String, String> params;
    private Map<String, String> output;
	private Resource customImageStreamsFile;
    private Resource customSecretsFile;
    private List<ActionDefinition> preActions;
}
