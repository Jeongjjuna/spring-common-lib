package yjh.ontongsal.common.web.api;

public interface ApiController {

    default <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data);
    }

    default <T> ApiResponse<T> success() {
        return success();
    }
}
