package com.vinylshop.config;

import com.vinylshop.service.PreferredCurrencyHolder;
import com.vinylshop.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public HandlerInterceptor preferredCurrencyClosedInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                PreferredCurrencyHolder.setCurrency(Constants.DEFAULT_CURRENCY);
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(preferredCurrencyClosedInterceptor());
    }
}
