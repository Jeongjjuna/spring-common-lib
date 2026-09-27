package yjh.ontongsal.common.domain.exception;

public sealed class NetworkException extends SystemException
        permits NetworkException.Http, NetworkException.Io {

    private final String clientName;
    private final HttpClientErrorType errorType;

    public static Http ofHttpError(String clientName, HttpClientErrorType errorType, int statusCode, String responseBody) {
        return new Http(clientName, errorType, statusCode, responseBody);
    }

    public String getClientName() {
        return clientName;
    }

    public HttpClientErrorType getErrorType() {
        return errorType;
    }

    private NetworkException(
            String clientName,
            HttpClientErrorType errorType,
            String message,
            Throwable cause
    ) {
        super(CommonResultCode.NETWORK_ERROR, cause);
        this.clientName = clientName;
        this.errorType = errorType;
    }

    public static final class Http extends NetworkException {
        private final int statusCode;
        private final String responseBody;

        private Http(
                String clientName,
                HttpClientErrorType errorType,
                int statusCode,
                String responseBody
        ) {
            super(clientName, errorType, "[%s] %s (status=%s)".formatted(clientName, errorType, statusCode), null);
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }

        public boolean is4xx() {
            return getErrorType() == HttpClientErrorType.HTTP_4XX;
        }

        public boolean is5xx() {
            return getErrorType() == HttpClientErrorType.HTTP_5XX;
        }

        public boolean is401() {
            return statusCode == 401;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getResponseBody() {
            return responseBody;
        }
    }

    public static final class Io extends NetworkException {
        private Io(String clientName, HttpClientErrorType errorType, Throwable cause) {
            super(clientName, errorType, "[%s] %s".formatted(clientName, errorType), cause);
        }

        public boolean isConnectTimeout() {
            return getErrorType() == HttpClientErrorType.CONNECT_TIMEOUT;
        }

        public boolean isReadTimeOut() {
            return getErrorType() == HttpClientErrorType.READ_TIMEOUT;
        }

        public boolean isPoolTimeOut() {
            return getErrorType() == HttpClientErrorType.POOL_TIMEOUT;
        }
    }

    public static Io ofIoError(
            String clientName,
            HttpClientErrorType errorType,
            Throwable cause
    ) {
        return new Io(clientName, errorType, cause);
    }
}
