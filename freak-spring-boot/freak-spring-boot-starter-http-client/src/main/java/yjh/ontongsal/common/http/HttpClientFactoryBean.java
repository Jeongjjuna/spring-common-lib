package yjh.ontongsal.common.http;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import yjh.ontongsal.common.domain.exception.HttpClientErrorType;
import yjh.ontongsal.common.domain.exception.NetworkException;
import yjh.ontongsal.common.http.interceptor.ExceptionMappingInterceptor;
import yjh.ontongsal.common.http.interceptor.LoggingClientInterceptor;
import yjh.ontongsal.common.http.interceptor.MdcPropagationInterceptor;
import yjh.ontongsal.common.http.proxy.HttpClientResilienceProxy;

import java.nio.charset.StandardCharsets;

public class HttpClientFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(HttpClientFactoryBean.class);

    private final Class<T> clientType;
    private final String clientName;
    private ApplicationContext applicationContext;

    public HttpClientFactoryBean(Class<T> clientType, String clientName) {
        this.clientType = clientType;
        this.clientName = clientName;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public @Nullable T getObject() throws Exception {
        var properties = applicationContext.getBean(HttpClientProperties.class);
        var config = properties.clients().get(clientName);
        if (config == null) {
            throw new IllegalStateException("freak.common.http.clients.%s 설정이 없습니다.".formatted(clientName));
        }

        var restClient = RestClient.builder()
                .baseUrl(config.baseUrl())
                .requestFactory(buildRequestFactory(config))
                .requestInterceptor(new LoggingClientInterceptor(clientName))
                .requestInterceptor(new MdcPropagationInterceptor())
                .requestInterceptor(new ExceptionMappingInterceptor(clientName))
                .defaultStatusHandler(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (_, response) -> {
                            int status = response.getStatusCode().value();
                            var errorType = status >= 500 ? HttpClientErrorType.HTTP_5XX : HttpClientErrorType.HTTP_4XX;
                            var body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            throw NetworkException.ofHttpError(clientName, errorType, status, body);
                        }
                )
                .build();

        var client = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(clientType);

        log.info("[HttpClient] '{}' RestClient 생성 완료 - baseUrl={}, connectTimeout={}, readTimeout={}, poolSize={}, connectionTtl={}, connectionRequestTimeout={}",
                clientName, config.baseUrl(), config.connectTimeout(), config.readTimeout(), config.poolSize(), config.connectionTtl(), config.connectionRequestTimeout());

        // proxy 로 한번 감싸주기
        return new HttpClientResilienceProxy<>(client, clientType, clientName, config).create();
    }

    public HttpComponentsClientHttpRequestFactory buildRequestFactory(HttpClientProperties.ClientConfig config) {
        var connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.poolSize());
        connectionManager.setDefaultMaxPerRoute(config.poolSize());
        connectionManager.setDefaultConnectionConfig(ConnectionConfig.custom()
                .setConnectTimeout(Timeout.of(config.connectTimeout()))
                .setTimeToLive(Timeout.of(config.connectionTtl()))
                .build());

        var httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setResponseTimeout(Timeout.of(config.readTimeout()))
                        .setConnectionRequestTimeout(Timeout.of(config.connectionRequestTimeout()))
                        .build())
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Override
    public @Nullable Class<?> getObjectType() {
        return clientType;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
