package org.kie.cloud.tests.loader.templates;

import java.util.Map;
import java.util.Optional;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "test.loaders.templates")
public class TemplateListConfiguration {

    private Map<String, TemplateDefinition> definitions;

    public Optional<TemplateDefinition> getTemplate(String name) {
        return Optional.ofNullable(definitions.get(name));
    }
}
