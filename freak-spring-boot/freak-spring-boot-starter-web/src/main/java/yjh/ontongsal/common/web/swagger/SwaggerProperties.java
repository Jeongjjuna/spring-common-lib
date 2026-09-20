package yjh.ontongsal.common.web.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "freak.common.web.swagger")
public record SwaggerProperties(
        @DefaultValue("") String description
) {
}
