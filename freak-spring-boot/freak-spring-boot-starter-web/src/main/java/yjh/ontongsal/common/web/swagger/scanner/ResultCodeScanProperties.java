package yjh.ontongsal.common.web.swagger.scanner;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "freak.common.web.swagger.result-code-scan")
public record ResultCodeScanProperties(
        @DefaultValue("true") boolean enabled
) {
}
