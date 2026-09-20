package yjh.ontongsal.common.web.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.json.JsonMapper;

@RestControllerAdvice
public class LoggingResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    public static final String RESPONSE_BODY_ATTRIBUTE = "logging.response.body";

    private final JsonMapper loggingJsonMapper = JsonMapper.builder()
            .addModule(new MaskedJacksonModule())
            .build();

    @Override
    public boolean supports(
            @NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    public @Nullable Object beforeBodyWrite(
            @Nullable Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {
        try {
            String maskedJson = loggingJsonMapper.writeValueAsString(body);
            if (request instanceof ServletServerHttpRequest servletRequest) {
                HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
                httpServletRequest.setAttribute(RESPONSE_BODY_ATTRIBUTE, maskedJson);
            }
        } catch (Exception e) {
            // logging failure should not affect response
        }

        return body;
    }
}
