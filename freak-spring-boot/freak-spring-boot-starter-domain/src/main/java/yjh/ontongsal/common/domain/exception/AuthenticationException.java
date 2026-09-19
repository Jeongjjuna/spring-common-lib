package yjh.ontongsal.common.domain.exception;

public class AuthenticationException extends RuntimeException {

    private final ResultCode resultCode;

    public AuthenticationException() {
        super(CommonResultCode.UNAUTHORIZED.getMessage());
        this.resultCode = CommonResultCode.UNAUTHORIZED;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
