package com.bestzyx.sso.server.managers.cache;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bestzyx.sso.server.config.SimpleSSOProperties;
import com.bestzyx.sso.server.managers.SessionManager;
import com.bestzyx.sso.server.token.UserAuthenticationToken;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;

import static com.bestzyx.sso.server.constant.AuthenticationConstant.SSO_SERVER_COOKIE_NAME;

/**
 * Created by zhangyongxiang on 2021/6/11 10:56 上午
 **/
@Slf4j
@Service
public class SessionCacheManager implements SessionManager {
    
    private static Cache<String, UserAuthenticationToken> tokenManager;
    
    private SimpleSSOProperties simpleSsoProperties;
    
    private Executor asyncExecutor = Executors.newWorkStealingPool();
    
    @Value("${server.servlet.context-path:/}")
    private String contextPath;
    
    public SessionCacheManager(final SimpleSSOProperties simpleSsoProperties) {
        this.simpleSsoProperties = simpleSsoProperties;
        tokenManager = Caffeine.newBuilder()
                .expireAfterAccess(
                        this.simpleSsoProperties.getSessionExpireTime())
                .expireAfterWrite(
                        this.simpleSsoProperties.getSessionExpireTime())
                .executor(asyncExecutor).recordStats().softValues()
                .removalListener((key, value, removalCause) -> log.info(
                        "session expire key: {}, value: {}, remove cause: {}",
                        key, value, removalCause))
                .scheduler(Scheduler.forScheduledExecutorService(
                        new ScheduledThreadPoolExecutor(
                                Runtime.getRuntime().availableProcessors())))
                .build();
    }
    
    @Override
    public UserAuthenticationToken getUserToken(final String sessionName) {
        return tokenManager.getIfPresent(sessionName);
    }
    
    @Override
    public UserAuthenticationToken addUserToken(final String ticket,
            final UserAuthenticationToken token) {
        log.info("new user login success, {}, {}", ticket, token);
        tokenManager.put(ticket, token);
        return token;
    }
    
    @Override
    public UserAuthenticationToken invalidate(final String sessionName) {
        final UserAuthenticationToken token = tokenManager
                .getIfPresent(sessionName);
        tokenManager.invalidate(sessionName);
        return token;
    }
    
    @Override
    public Cookie createSsoCookie(final String cookieValue) {
        final Cookie cookie = new Cookie(SSO_SERVER_COOKIE_NAME, cookieValue);
        cookie.setPath(contextPath);
        cookie.setMaxAge(Math.toIntExact(
                simpleSsoProperties.getCookieExpireTime().getSeconds()));
        cookie.setComment("sso cookie");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
