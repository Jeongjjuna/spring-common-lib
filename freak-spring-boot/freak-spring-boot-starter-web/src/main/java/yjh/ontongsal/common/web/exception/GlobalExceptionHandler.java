package yjh.ontongsal.common.web.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import yjh.ontongsal.common.domain.exception.*;
import yjh.ontongsal.common.web.api.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.info("[{}] {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.ok(ApiResponse.error(e.getResultCode()));
    }

    @ExceptionHandler(NetworkException.class)
    public ResponseEntity<ApiResponse<Void>> handleNetworkException(NetworkException e) {
        switch (e) {
            case NetworkException.Http http -> {
                if (http.is4xx()) {
                    log.warn("[NetworkException] client={}, type={}, status={}", http.getClientName(), http.getErrorType(), http.getStatusCode(), http);
                } else {
                    log.error("[NetworkException] client={}, type={}, status={}", http.getClientName(), http.getErrorType(), http.getStatusCode(), http);
                }
            }
            case NetworkException.Io io -> {
                log.error("[NetworkException] client={}, type={}", io.getClientName(), io.getErrorType(), io);
            }
            default -> log.error("[NetworkException] client={}, type={}", e.getClientName(), e.getErrorType());
        }
        return ResponseEntity.ok(ApiResponse.error(e.getResultCode()));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(SystemException e) {
        log.error("[{}] {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return ResponseEntity.ok(ApiResponse.error(e.getResultCode()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.info("[{}] {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getResultCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse(CommonResultCode.INVALID_REQUEST.getMessage());
        log.warn("[{}] {}", e.getClass().getSimpleName(), message);
        return ResponseEntity.ok(ApiResponse.error(CommonResultCode.INVALID_REQUEST, message));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        return invalidRequest(e);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        return invalidRequest(e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        return invalidRequest(e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return invalidRequest(e);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return invalidRequest(e);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPathVariableException(MissingPathVariableException e) {
        return invalidRequest(e);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return invalidRequest(e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[{}] {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return ResponseEntity.ok(ApiResponse.error(CommonResultCode.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<ApiResponse<Void>> invalidRequest(Exception e) {
        String message = e.getMessage();
        log.warn("[{}] {}", e.getClass().getSimpleName(), message);
        return ResponseEntity.ok(ApiResponse.error(CommonResultCode.INVALID_REQUEST, message));
    }
}
