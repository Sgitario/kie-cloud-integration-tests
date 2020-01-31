package org.kie.cloud.tests.config.templates;

import java.util.Map;
import java.util.Optional;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "template")
@Data
public class TemplateListConfiguration {

    private Map<String, TemplateDefinition> definitions;

    public Optional<TemplateDefinition> getTemplate(String name) {
        return Optional.ofNullable(definitions.get(name));
    }
}
