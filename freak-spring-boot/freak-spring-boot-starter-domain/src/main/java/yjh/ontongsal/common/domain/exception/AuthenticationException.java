package yjh.ontongsal.common.domain.exception;

public class AuthenticationException extends RuntimeException {

    private final ResultCode resultCode;

    public AuthenticationException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
