package yjh.ontongsal.common.web.api;

import org.slf4j.MDC;
import yjh.ontongsal.common.domain.exception.ResultCode;
import yjh.ontongsal.common.web.filter.MdcFilter;

/**
 * ApiResponse<ProductResponse>
 * ApiResponse<List<ProductResponse>>
 * ApiResponse<Void>
 */
public record ApiResponse<T>(
        int result_code,
        String result_message,
        T data,
        String trace_id
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(1, "SUCCESS", data, MDC.get(MdcFilter.TRACE_ID));
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static ApiResponse<Void> error(ResultCode resultCode) {
        return new ApiResponse<>(resultCode.getCode(), resultCode.getMessage(), null, MDC.get(MdcFilter.TRACE_ID));
    }

    public static ApiResponse<Void> error(ResultCode resultCode, String message) {
        return new ApiResponse<>(resultCode.getCode(), message, null, MDC.get(MdcFilter.TRACE_ID));
    }
}
