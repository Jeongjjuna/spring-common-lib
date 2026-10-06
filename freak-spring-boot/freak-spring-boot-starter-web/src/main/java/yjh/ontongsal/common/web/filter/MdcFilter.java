package yjh.ontongsal.common.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import yjh.ontongsal.common.domain.tracing.FreakTracing;

import java.io.IOException;
import java.util.UUID;

public class MdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String traceId = request.getHeader(FreakTracing.TRACE_ID_HEADER);

            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString();
            }

            MDC.put(FreakTracing.TRACE_ID_MDC_KEY, traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(FreakTracing.TRACE_ID_MDC_KEY);
        }
    }
}
