package yjh.ontongsal.common.web.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;

@RestControllerAdvice
public class LoggingRequestBodyAdvice implements RequestBodyAdvice {
    public static final String REQUEST_BODY_ATTRIBUTE = "logging.request.body";

    private final JsonMapper loggingJsonMapper = JsonMapper.builder()
            .addModule(new MaskedJacksonModule())
            .build();

    @Override
    public boolean supports(
            @NonNull MethodParameter methodParameter,
            @NonNull Type targetType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(
            @NonNull HttpInputMessage inputMessage,
            @NonNull MethodParameter parameter,
            @NonNull Type targetType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(
            @NonNull Object body,
            @NonNull HttpInputMessage inputMessage,
            @NonNull MethodParameter parameter,
            @NonNull Type targetType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {
        try {
            String maskedJson = loggingJsonMapper.writeValueAsString(body);
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                attributes.setAttribute(REQUEST_BODY_ATTRIBUTE, maskedJson, RequestAttributes.SCOPE_REQUEST);
            }
        } catch (Exception e) {
            // 로깅 실패가 실제 요청 처리에 영향을 주면 안 됨
        }

        return body;
    }

    @Override
    public @Nullable Object handleEmptyBody(@Nullable Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
