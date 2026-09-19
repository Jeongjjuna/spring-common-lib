package yjh.ontongsal.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yjh.ontongsal.web.api.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException e
    ) {
        log.info("Business exception", e);

        ResultCode resultCode = e.getResultCode();

        return ResponseEntity
                .ok(new ApiResponse<>(
                        resultCode.getCode(),
                        resultCode.getMessage(),
                        null,
                        MDC.get("traceId")
                ));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(
            SystemException e
    ) {
        log.info("System exception", e);

        ResultCode resultCode = e.getResultCode();

        return ResponseEntity
                .ok(new ApiResponse<>(
                        resultCode.getCode(),
                        resultCode.getMessage(),
                        null,
                        MDC.get("traceId")
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException e
    ) {
        log.info("AuthenticationException exception", e);

        ResultCode resultCode = e.getResultCode();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED.value())
                .body(new ApiResponse<>(
                        resultCode.getCode(),
                        resultCode.getMessage(),
                        null,
                        MDC.get("traceId")
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception e
    ) {
        log.error("Unexpected exception", e);

        return ResponseEntity
                .ok()
                .body(new ApiResponse<>(
                        99999,
                        "Internal server error",
                        null,
                        MDC.get("traceId")
                ));
    }
}
