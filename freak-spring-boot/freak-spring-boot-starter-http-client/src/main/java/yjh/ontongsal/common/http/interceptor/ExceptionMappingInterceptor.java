package yjh.ontongsal.common.http.interceptor;

import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import yjh.ontongsal.common.domain.exception.HttpClientErrorType;
import yjh.ontongsal.common.domain.exception.NetworkException;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class ExceptionMappingInterceptor implements ClientHttpRequestInterceptor {

    private final String clientName;

    public ExceptionMappingInterceptor(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            byte @NonNull [] body,
            @NonNull ClientHttpRequestExecution execution
    ) {
        try {
            return execution.execute(request, body);
        } catch (ConnectionRequestTimeoutException e) {
            throw NetworkException.ofIoError(clientName, HttpClientErrorType.POOL_TIMEOUT, e);
        } catch (ConnectTimeoutException e) {
            // ConnectTimeoutException extends SocketTimeoutException
            throw NetworkException.ofIoError(clientName, HttpClientErrorType.CONNECT_TIMEOUT, e);
        } catch (SocketTimeoutException e) {
            throw NetworkException.ofIoError(clientName, HttpClientErrorType.READ_TIMEOUT, e);
        } catch (IOException e) {
            throw NetworkException.ofIoError(clientName, HttpClientErrorType.IO_ERROR, e);
        } catch (Exception e) {
            throw NetworkException.ofIoError(clientName, HttpClientErrorType.UNKNOWN, e);
        }
    }
}
