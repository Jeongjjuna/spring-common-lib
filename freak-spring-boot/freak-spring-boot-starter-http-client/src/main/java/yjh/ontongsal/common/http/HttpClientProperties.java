package yjh.ontongsal.common.http;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.Map;

@ConfigurationProperties(prefix = "freak.common.http")
public record HttpClientProperties(
        @DefaultValue Map<String, ClientConfig> clients
) {

    public HttpClientProperties {
        clients = clients != null ? Map.copyOf(clients) : Map.of();
    }

    public record ClientConfig(
            String baseUrl,
            @DefaultValue("3s") Duration connectTimeout,
            @DefaultValue("10s") Duration readTimeout,
            @DefaultValue("50") int poolSize,
            @DefaultValue("25s") Duration connectionTtl,
            @DefaultValue("5s") Duration connectionRequestTimeout,
            @Nullable RetryConfig retry,
            @Nullable Duration circuitBreaker
    ) {
    }

    public record RetryConfig(
            @DefaultValue("false") boolean enabled,
            @DefaultValue("3") int maxAttempts
    ) {
    }

    public record CircuitBreakerConfig(
            @DefaultValue("false") boolean enabled
    ) {
    }
}
