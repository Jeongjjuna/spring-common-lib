package yjh.ontongsal.common.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import tools.jackson.databind.json.JsonMapper;
import yjh.ontongsal.common.domain.exception.CommonResultCode;
import yjh.ontongsal.common.web.exception.GlobalExceptionHandler;
import yjh.ontongsal.common.web.filter.*;
import yjh.ontongsal.common.web.swagger.SwaggerProperties;
import yjh.ontongsal.common.web.swagger.scanner.ResultCodeScanProperties;
import yjh.ontongsal.common.web.swagger.scanner.ResultCodeScanner;
import yjh.ontongsal.common.web.swagger.scanner.auto.AutoResultCodeScanner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
@ConditionalOnProperty(
        prefix = "freak.common.web",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class WebAutoConfiguration {

    private final JsonMapper jsonMapper;

    public WebAutoConfiguration(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Bean
    @ConditionalOnMissingBean(name = "globalExceptionHandler")
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name = "loggingRequestBodyAdvice")
    public LoggingRequestBodyAdvice loggingRequestBodyAdvice() {
        return new LoggingRequestBodyAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(name = "loggingResponseBodyAdvice")
    public LoggingResponseBodyAdvice loggingResponseBodyAdvice() {
        return new LoggingResponseBodyAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(name = "mdcFilter")
    public FilterRegistrationBean<MdcFilter> mdcFilter() {
        FilterRegistrationBean<MdcFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MdcFilter());
        registration.setOrder(FreakFilterOrder.MDC);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "loggingFilter")
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LoggingFilter(jsonMapper));
        registration.setOrder(FreakFilterOrder.LOGGING);
        return registration;
    }

    @EnableConfigurationProperties({SwaggerProperties.class, ResultCodeScanProperties.class})
    @ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true")
    static class SwaggerConfiguration {
        private static final Logger log = LoggerFactory.getLogger(SwaggerConfiguration.class);

        @Bean
        @ConditionalOnMissingBean(name = "defaultOpenApiInfoCustomizer")
        public OpenApiCustomizer defaultOpenApiInfoCustomizer(Environment environment, SwaggerProperties swaggerProperties) {
            String appName = environment.getProperty("spring.application.name", "API");

            String commonResultCodes = Arrays.stream(CommonResultCode.values())
                    .map(resultCode -> String.format(
                            "| %d | %s |",
                            resultCode.getCode(),
                            resultCode.getMessage()
                    ))
                    .collect(Collectors.joining("\n"));

            String description = """
                    ### 공통 응답 코드
                    
                    필터/인터셉터 등 모든 API에 전역적으로 발생 가능한 코드입니다.
                    
                    | result code | 메시지 |
                    |-------------|--------|
                    %s
                    
                    ---
                    ### HTTP Status
                    
                    API에서 발생 가능한 HTTP Status는 다음과 같습니다.
                    
                    | Status | 설명 |
                    |--------|------|
                    | 200 | 정상적인 API 응답 |
                    | 401 | 인증이 필요한 요청 또는 인증 실패 |
                    
                    ---
                    ### 공통 Response Body
                    
                    모든 API Response Body는 다음과 같은 공통 형식을 사용합니다.
                    
                    | 필드 | 타입 | 설명 |
                    |------|------|------|
                    | result_code | int | 응답 코드 |
                    | result_message | string | 응답 메시지 |
                    | data | T | API 응답 데이터. 응답 데이터가 없는 경우 null |
                    | trace_id | string | 요청 추적을 위한 Trace ID |
                    
                    <pre>
                    {
                      "result_code": 1,
                      "result_message": "SUCCESS",
                      "data": null,
                      "trace_id": "550e8400-e29b-41d4-a716-446655440000"
                    }
                    </pre>
                    
                    ---
                    %s
                    """.formatted(
                    commonResultCodes,
                    swaggerProperties.description()
            );

            return openApi -> openApi.info(
                    new Info()
                            .title(appName)
                            .description(description)
            );
        }

        @Bean
        @ConditionalOnProperty(
                prefix = "freak.common.web.swagger.result-code-scan",
                name = "enabled",
                havingValue = "true",
                matchIfMissing = true
        )
        public ResultCodeScanner resultCodeScanner(BeanFactory beanFactory) {
            List<String> packages = AutoConfigurationPackages.get(beanFactory);
            String basePackage = packages.isEmpty() ? "" : packages.getFirst();

            ResultCodeScanner scanner = new AutoResultCodeScanner();
            scanner.initializeResultCode(basePackage);
            log.info("ResultCodeScanner initialized [basePackage={}]", basePackage);
            return scanner;
        }

        @Bean
        @ConditionalOnMissingBean(name = "resultCodeOperationCustomizer")
        public OperationCustomizer resultCodeOperationCustomizer(ResultCodeScanner resultCodeScanner) {
            return resultCodeScanner::applyResultCodeToSwagger;
        }

        @Bean
        @ConditionalOnMissingBean(name = "jwtSecurityOpenApiCustomizer")
        public OpenApiCustomizer jwtSecurityOpenApiCustomizer() {
            return openApi -> {
                var securityScheme = new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization");

                var components = openApi.getComponents() != null
                        ? openApi.getComponents()
                        : new Components();

                openApi.components(components.addSecuritySchemes("bearer-key", securityScheme));
            };
        }
    }
}
