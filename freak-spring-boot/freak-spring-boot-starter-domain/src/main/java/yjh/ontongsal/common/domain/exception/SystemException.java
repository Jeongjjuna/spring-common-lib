package yjh.ontongsal.common.domain.exception;

public class SystemException extends RuntimeException {

    private final ResultCode resultCode;

    public SystemException(ResultCode resultCode) {
        this(resultCode, resultCode.getMessage());
    }

    public SystemException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public SystemException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.resultCode = resultCode;
    }

    public SystemException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
