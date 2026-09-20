package yjh.ontongsal.common.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;
import yjh.ontongsal.common.domain.exception.AuthenticationException;
import yjh.ontongsal.common.domain.exception.BusinessException;
import yjh.ontongsal.common.domain.exception.CommonResultCode;
import yjh.ontongsal.common.domain.exception.SystemException;
import yjh.ontongsal.common.web.api.ApiResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

import static yjh.ontongsal.common.web.filter.LoggingRequestBodyAdvice.REQUEST_BODY_ATTRIBUTE;
import static yjh.ontongsal.common.web.filter.LoggingResponseBodyAdvice.RESPONSE_BODY_ATTRIBUTE;

public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private static final int MAX_BODY_LOG_SIZE = 1000;
    private static final String NON_TEXT_BODY = "(non-text)";
    private static final String BODY_TOO_LARGE = "(body-too-large)";

    private final JsonMapper jsonMapper;

    public LoggingFilter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            log.debug("AuthenticationException in filter chain", e);
            writeResponse(response, HttpStatus.UNAUTHORIZED, ApiResponse.error(e.getResultCode()));
        } catch (BusinessException e) {
            log.debug("BusinessException in filter chain", e);
            writeResponse(response, HttpStatus.OK, ApiResponse.error(e.getResultCode()));
        } catch (SystemException e) {
            log.error("SystemException in filter chain", e);
            writeResponse(response, HttpStatus.OK, ApiResponse.error(e.getResultCode()));
        } catch (Exception e) {
            log.error("Exception in filter chain", e);
            writeResponse(response, HttpStatus.OK, ApiResponse.error(CommonResultCode.INTERNAL_SERVER_ERROR));
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;
            var filterLog = buildLogJson(request, response, durationMs);
            log.info(filterLog);
        }
    }

    private void writeResponse(HttpServletResponse response, HttpStatus status, ApiResponse<?> body) throws IOException {
        response.reset();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(jsonMapper.writeValueAsString(body));
    }

    private String buildLogJson(
            HttpServletRequest request,
            HttpServletResponse response,
            long durationMs
    ) {
        var log = new LinkedHashMap<String, Object>();
        log.put("method", request.getMethod());
        log.put("uri", request.getRequestURI());
        log.put("query", request.getQueryString());
        log.put("status", response.getStatus());
        log.put("duration_ms", durationMs);
        log.put("request_body", extractLogBody(request.getAttribute(REQUEST_BODY_ATTRIBUTE), request.getContentType()));
        log.put("response_body", extractLogBody(request.getAttribute(RESPONSE_BODY_ATTRIBUTE), response.getContentType()));
        return jsonMapper.writeValueAsString(log);
    }

    private Object extractLogBody(
            Object body,
            String contentType
    ) {
        if (body == null) {
            return "";
        }

        if (!isLoggableContentType(contentType)) {
            return NON_TEXT_BODY;
        }

        String bodyString = body.toString();

        if (bodyString.length() > MAX_BODY_LOG_SIZE) {
            return BODY_TOO_LARGE;
        }

        if (contentType.contains("application/json")) {
            try {
                return jsonMapper.readTree(bodyString);
            } catch (Exception ignored) {
                // JSON 파싱 실패 시 원본 문자열 사용
            }
        }
        return bodyString;
    }

    private boolean isLoggableContentType(String contentType) {
        return contentType != null
                && (contentType.contains("application/json") || contentType.contains("text/")
        );
    }
}
