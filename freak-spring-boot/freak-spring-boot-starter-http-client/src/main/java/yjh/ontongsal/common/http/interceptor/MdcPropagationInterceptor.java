package yjh.ontongsal.common.http.interceptor;

import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import yjh.ontongsal.common.domain.tracing.FreakTracing;

import java.io.IOException;

public class MdcPropagationInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            byte @NonNull [] body,
            @NonNull ClientHttpRequestExecution execution
    ) throws IOException {
        String traceId = MDC.get(FreakTracing.TRACE_ID_MDC_KEY);
        if (traceId != null) {
            request.getHeaders().set(FreakTracing.TRACE_ID_HEADER, traceId);
        }
        return execution.execute(request, body);
    }
}
