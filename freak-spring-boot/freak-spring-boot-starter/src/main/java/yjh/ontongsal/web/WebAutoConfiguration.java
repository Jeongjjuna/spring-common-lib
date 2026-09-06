package yjh.ontongsal.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import yjh.ontongsal.web.filter.LoggingFilter;
import yjh.ontongsal.web.filter.MdcFilter;

@AutoConfiguration
public class WebAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "freak.lib.web.filter.mdc",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public FilterRegistrationBean<MdcFilter> mdcFilter() {
        FilterRegistrationBean<MdcFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(new MdcFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);

        return registration;
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "freak.lib.web.filter.logging",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(new LoggingFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 101);

        return registration;
    }
}
