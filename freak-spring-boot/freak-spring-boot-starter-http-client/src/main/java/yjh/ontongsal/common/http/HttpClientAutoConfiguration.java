package yjh.ontongsal.common.http;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(HttpClientProperties.class)
@ConditionalOnProperty(
        prefix = "freak.common.web",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class HttpClientAutoConfiguration {

    @Bean
    public HttpClientBeanDefinitionRegistrar httpClientBeanDefinitionRegistrar() {
        return new HttpClientBeanDefinitionRegistrar();
    }
}
