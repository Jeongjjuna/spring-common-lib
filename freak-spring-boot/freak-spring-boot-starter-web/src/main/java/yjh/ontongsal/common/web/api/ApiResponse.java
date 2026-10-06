package yjh.ontongsal.common.web.api;

import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.MDC;
import yjh.ontongsal.common.domain.exception.ResultCode;
import yjh.ontongsal.common.domain.tracing.FreakTracing;

/**
 * ApiResponse<ProductResponse>
 * ApiResponse<List<ProductResponse>>
 * ApiResponse<Void>
 */
public record ApiResponse<T>(
        @Schema(example = "1") int result_code,
        @Schema(example = "SUCCESS") String result_message,
        @Schema(nullable = true, example = "null") T data,
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000") String trace_id
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(1, "SUCCESS", data, MDC.get(FreakTracing.TRACE_ID_MDC_KEY));
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static ApiResponse<Void> error(ResultCode resultCode) {
        return new ApiResponse<>(resultCode.getCode(), resultCode.getMessage(), null, MDC.get(FreakTracing.TRACE_ID_MDC_KEY));
    }

    public static ApiResponse<Void> error(ResultCode resultCode, String message) {
        return new ApiResponse<>(resultCode.getCode(), message, null, MDC.get(FreakTracing.TRACE_ID_MDC_KEY));
    }
}
