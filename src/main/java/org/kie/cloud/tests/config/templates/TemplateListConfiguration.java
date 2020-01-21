package org.kie.cloud.tests.config.templates;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "template")
@Data
public class TemplateListConfiguration {
	private List<TemplateDefinition> definitions;

	public Optional<TemplateDefinition> getTemplate(String name) {
		return definitions.stream().filter(def -> def.getName().equals(name)).findFirst();
	}
}
