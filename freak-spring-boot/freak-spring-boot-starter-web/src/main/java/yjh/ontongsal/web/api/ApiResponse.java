package yjh.ontongsal.web.api;

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
}
