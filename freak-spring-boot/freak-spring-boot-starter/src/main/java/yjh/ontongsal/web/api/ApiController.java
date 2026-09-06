package yjh.ontongsal.web.api;

import org.slf4j.MDC;

public interface ApiController {

    default <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                1,
                "SUCCESS",
                data,
                MDC.get("traceId")
        );
    }

    default <T> ApiResponse<T> success() {
        return success(null);
    }
}
