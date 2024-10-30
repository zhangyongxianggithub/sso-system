package com.bestzyx.sso.client;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bestzyx.sso.client.auth.UrlConverter;
import com.bestzyx.sso.client.config.AuthenticationProperties;
import com.bestzyx.sso.client.filters.SimpleSSOLoginFilter;
import com.bestzyx.sso.client.filters.SimpleSSOLogoutFilter;
import com.bestzyx.sso.client.listeners.CompositeHttpSessionListener;
import com.bestzyx.sso.client.listeners.LocalHttpSessionListener;
import com.bestzyx.sso.client.listeners.LogHttpSessionListener;

import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.nonNull;

/**
 * Created by zhangyongxiang on 2021/6/10 11:55 下午
 **/
@Slf4j
@ConditionalOnWebApplication
@EnableConfigurationProperties(AuthenticationProperties.class)
@Configuration
public class SimpleSSOAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public HttpSessionListener httpSessionListener() {
        final CompositeHttpSessionListener listener = new CompositeHttpSessionListener();
        listener.addListener(new LogHttpSessionListener());
        listener.addListener(new LocalHttpSessionListener());
        if (log.isDebugEnabled()) {
            log.debug("init a http session listener,{}", listener);
        }
        return listener;
    }
    
    @Bean
    @ConditionalOnBean(HttpSessionListener.class)
    public ServletListenerRegistrationBean<HttpSessionListener> servletListenerRegistrationBean(
            final HttpSessionListener httpSessionListener) {
        final ServletListenerRegistrationBean<HttpSessionListener> listenerRegBean =
                new ServletListenerRegistrationBean<>();
        listenerRegBean.setListener(httpSessionListener);
        return listenerRegBean;
    }
    
    @Bean
    @ConditionalOnMissingFilterBean
    public FilterRegistrationBean<SimpleSSOLoginFilter> loginFilter(
            final AuthenticationProperties authenticationProperties,
            @Value("${server.servlet.context-path:}") final String contextPath,
            @Autowired(required = false) final UrlConverter urlConverter) {
        final FilterRegistrationBean<SimpleSSOLoginFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SimpleSSOLoginFilter(
                authenticationProperties, contextPath, urlConverter));
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        registration.setName("ssoLoginAuthenticationFilter");
        if (log.isDebugEnabled()) {
            log.debug("register login filter, url pattern is {}",
                    registration.getUrlPatterns());
        }
        return registration;
    }
    
    @Bean
    @ConditionalOnMissingFilterBean
    public FilterRegistrationBean<SimpleSSOLogoutFilter> logoutFilter(
            final AuthenticationProperties authenticationProperties) {
        final FilterRegistrationBean<SimpleSSOLogoutFilter> registration = new FilterRegistrationBean<>();
        registration
                .setFilter(new SimpleSSOLogoutFilter(authenticationProperties));
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        registration.setName("ssoLogoutFilter");
        if (log.isDebugEnabled()) {
            log.debug("register logout filter, url pattern is {}",
                    registration.getUrlPatterns());
        }
        return registration;
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("spring.sso.gateway.enabled")
    public UrlConverter urlConverter(
            final AuthenticationProperties authenticationProperties) {
        if (nonNull(
                authenticationProperties.getGateway().getConverterClass())) {
            if (UrlConverter.class.isAssignableFrom(authenticationProperties
                    .getGateway().getConverterClass())) {
                
                try {
                    return authenticationProperties.getGateway()
                            .getConverterClass().getDeclaredConstructor()
                            .newInstance();
                } catch (final Exception e) {
                    throw new IllegalStateException(e);
                }
            } else {
                throw new InvalidConfigurationPropertyValueException(
                        "spring.sso.gateway.converter-class",
                        authenticationProperties.getGateway()
                                .getConverterClass().getCanonicalName(),
                        "you must specify a class that implement UrlConverter");
            }
        } else {
            throw new InvalidConfigurationPropertyValueException(
                    "spring.sso.gateway.converter-class", "",
                    "you must specify a class that implement UrlConverter");
        }
    }
}
