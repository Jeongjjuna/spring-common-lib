package yjh.ontongsal.common.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.json.JsonMapper;
import yjh.ontongsal.common.web.exception.GlobalExceptionHandler;
import yjh.ontongsal.common.web.filter.FreakFilterOrder;
import yjh.ontongsal.common.web.filter.LoggingFilter;
import yjh.ontongsal.common.web.filter.MdcFilter;

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
}
