package yjh.ontongsal.common.domain.exception;

public enum CommonResultCode implements ResultCode {

    UNAUTHORIZED(99996, "Authentication required"),
    INVALID_REQUEST(99997, "Invalid parameter"),
    NETWORK_ERROR(99998, "Internal network error"),
    INTERNAL_SERVER_ERROR(99999, "Internal server error");

    private final int code;
    private final String message;

    CommonResultCode(int code, String message) {
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
