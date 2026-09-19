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
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import tools.jackson.databind.json.JsonMapper;
import yjh.ontongsal.common.domain.exception.AuthenticationException;
import yjh.ontongsal.common.domain.exception.BusinessException;
import yjh.ontongsal.common.domain.exception.CommonResultCode;
import yjh.ontongsal.common.domain.exception.SystemException;
import yjh.ontongsal.common.web.api.ApiResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private static final int MAX_BODY_LOG_SIZE = 2000;

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
        var wrappedRequest = new ContentCachingRequestWrapper(request, MAX_BODY_LOG_SIZE);
        var wrappedResponse = new ContentCachingResponseWrapper(response);
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            log.debug("AuthenticationException in filter chain", e);
            writeResponse(wrappedResponse, HttpStatus.UNAUTHORIZED, ApiResponse.error(e.getResultCode()));
        } catch (BusinessException e) {
            log.debug("BusinessException in filter chain", e);
            writeResponse(wrappedResponse, HttpStatus.OK, ApiResponse.error(e.getResultCode()));
        } catch (SystemException e) {
            log.error("SystemException in filter chain", e);
            writeResponse(wrappedResponse, HttpStatus.OK, ApiResponse.error(e.getResultCode()));
        } catch (Exception e) {
            log.error("Exception in filter chain", e);
            writeResponse(wrappedResponse, HttpStatus.OK, ApiResponse.error(CommonResultCode.INTERNAL_SERVER_ERROR));
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;
            var filterLog = buildLogJson(wrappedRequest, wrappedResponse, durationMs);
            log.info(filterLog);
        }
    }

    private void writeResponse(ContentCachingResponseWrapper response, HttpStatus status, ApiResponse<?> body) throws IOException {
        response.reset();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(jsonMapper.writeValueAsString(body));
    }

    private String buildLogJson(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            long durationMs
    ) {
        var log = new LinkedHashMap<String, Object>();

        log.put("method", request.getMethod());
        log.put("uri", request.getRequestURI());
        log.put("query", request.getQueryString());
        log.put("status", response.getStatus());
        log.put("duration_ms", durationMs);

        log.put("request_body", getContent(request.getContentAsByteArray()));
        log.put("response_body", getContent(response.getContentAsByteArray()));

        return jsonMapper.writeValueAsString(log);
    }

    private String getContent(byte[] content) {
        if (content.length == 0) {
            return null;
        }

        return new String(content, StandardCharsets.UTF_8);
    }
}
