package yjh.ontongsal.common.http.proxy;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.service.annotation.*;
import yjh.ontongsal.common.domain.exception.NetworkException;
import yjh.ontongsal.common.http.HttpClientProperties;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

public class HttpClientResilienceProxy<T> {

    private static final Logger log = LoggerFactory.getLogger(HttpClientResilienceProxy.class);

    private static final Set<HttpMethod> DEFAULT_RETRY_METHODS = Set.of(
            HttpMethod.GET,
            HttpMethod.HEAD,
            HttpMethod.OPTIONS
    );

    private final T target;
    private final Class<T> clientType;
    private final String clientName;
    private final HttpClientProperties.ClientConfig config;

    public HttpClientResilienceProxy(
            T target,
            Class<T> clientType,
            String clientName,
            HttpClientProperties.ClientConfig config
    ) {
        this.target = target;
        this.clientType = clientType;
        this.clientName = clientName;
        this.config = config;
    }

    public T create() {
        var retryConfig = config.retry();

        // 1. application.yml 에 활성화되지 않았다면, proxy 로 감싸지 않는다.
        if (retryConfig == null) {
            return target;
        }
        if (!retryConfig.enabled()) {
            return target;
        }

        // 2. retry 동작을 수행하는 proxy 객체 반환.
        var retry = Retry.of(
                clientName,
                RetryConfig.custom()
                        .maxAttempts(retryConfig.maxAttempts())         // 시도 횟수
                        .intervalFunction(
                                IntervalFunction.ofExponentialBackoff(  // 지수백오프
                                        retryConfig.waitDuration()
                                )
                        )
                        .retryOnException(this::isRetryableException)
                        .build()
        );

        retry.getEventPublisher()
                .onRetry(event -> {
                    var throwable = event.getLastThrowable();

                    var cause = throwable instanceof NetworkException e
                            ? e.getCause()
                            : throwable;

                    log.warn(
                            "HTTP Client retry. client={}, attempt={}/{}, cause={}, rootCause={}",
                            clientName,
                            event.getNumberOfRetryAttempts() + 1,
                            retryConfig.maxAttempts(),
                            throwable instanceof NetworkException e
                                    ? e.getErrorType()
                                    : "unknown",
                            cause != null
                                    ? cause.getClass().getSimpleName() + ": " + cause.getMessage()
                                    : "unknown"
                    );
                });

        InvocationHandler handler = (_, method, args) -> {
            if (isRetryableMethod(retryConfig.retryOnPost(), method)) {
                return retry.executeSupplier(() -> invoke(method, args));
            }

            return invoke(method, args);
        };

        var proxy = Proxy.newProxyInstance(
                clientType.getClassLoader(),
                new Class<?>[]{clientType},
                handler
        );
        return clientType.cast(proxy);
    }

    private boolean isRetryableException(Throwable throwable) {
        if (!(throwable instanceof NetworkException e)) {
            return false;
        }

        return switch (e.getErrorType()) {
            case CONNECT_TIMEOUT,
                 READ_TIMEOUT,
                 POOL_TIMEOUT,
                 IO_ERROR -> true;

            case HTTP_4XX,
                 HTTP_5XX,
                 UNKNOWN -> false;
        };
    }

    private boolean isRetryableMethod(boolean isRetryOnPost, Method method) {
        var httpMethod = resolveHttpMethod(method);

        if (httpMethod == null) {
            return false;
        }

        return DEFAULT_RETRY_METHODS.contains(httpMethod) || (httpMethod == HttpMethod.POST && isRetryOnPost);
    }

    private @Nullable HttpMethod resolveHttpMethod(Method method) {
        if (method.isAnnotationPresent(GetExchange.class)) {
            return HttpMethod.GET;
        }

        if (method.isAnnotationPresent(PostExchange.class)) {
            return HttpMethod.POST;
        }

        if (method.isAnnotationPresent(PutExchange.class)) {
            return HttpMethod.PUT;
        }

        if (method.isAnnotationPresent(PatchExchange.class)) {
            return HttpMethod.PATCH;
        }

        if (method.isAnnotationPresent(DeleteExchange.class)) {
            return HttpMethod.DELETE;
        }

        var httpExchange = method.getAnnotation(HttpExchange.class);

        if (httpExchange != null && !httpExchange.method().isBlank()) {
            return HttpMethod.valueOf(httpExchange.method());
        }

        return null;
    }

    private Object invoke(Method method, Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw sneakyThrow(e.getCause()); // e 가 nullable 타입이긴 하지만, 실질적으로 발생할일이 없어서 방어처리하지 않음.
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("HTTP Client 호출 실패: " + method, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> RuntimeException sneakyThrow(Throwable throwable) throws E {
        throw (E) throwable;
    }
}
