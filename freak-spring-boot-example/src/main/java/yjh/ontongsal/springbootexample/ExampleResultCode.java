package yjh.ontongsal.springbootexample;

import yjh.ontongsal.common.domain.exception.ResultCode;

public enum ExampleResultCode implements ResultCode {
    NOT_FOUNT(44444, "404 찾을 수 없음"),
    INTERNAL_SERVER_ERROR(55555, "500 서버 내부 에러."),
    INVALID_PARAMETER(40001, "잘못된 요청 파라미터입니다."),
    UNAUTHORIZED(40002, "인증되지 않은 요청입니다."),
    FORBIDDEN(40003, "접근 권한이 없습니다."),
    RESOURCE_NOT_FOUND(40004, "요청한 리소스를 찾을 수 없습니다."),
    CONFLICT(40009, "요청이 현재 상태와 충돌합니다."),
    TOO_MANY_REQUESTS(40029, "요청이 너무 많습니다."),
    SERVICE_UNAVAILABLE(50003, "서비스를 일시적으로 사용할 수 없습니다.");

    private final int code;
    private final String message;

    ExampleResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
