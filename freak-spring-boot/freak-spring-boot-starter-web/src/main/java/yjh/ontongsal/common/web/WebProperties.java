package yjh.ontongsal.common.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "freak.common.web")
public record WebProperties(
        @DefaultValue("true") boolean enabled
) {
}
