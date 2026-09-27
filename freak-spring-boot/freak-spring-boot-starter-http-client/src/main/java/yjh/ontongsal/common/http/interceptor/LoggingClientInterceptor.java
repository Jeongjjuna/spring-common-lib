package yjh.ontongsal.common.http.interceptor;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class LoggingClientInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingClientInterceptor.class);

    private final String clientName;

    public LoggingClientInterceptor(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            byte @NonNull [] body,
            @NonNull ClientHttpRequestExecution execution
    ) throws IOException {
        log.info("[{}] -> {} {}", clientName, request.getMethod(), request.getURI());
        var response = execution.execute(request, body);
        log.info("[{}] <- {}", clientName, response.getStatusCode().value());
        return response;
    }
}
