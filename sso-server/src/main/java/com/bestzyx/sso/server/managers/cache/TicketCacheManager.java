package com.bestzyx.sso.server.managers.cache;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bestzyx.sso.server.config.SimpleSSOProperties;
import com.bestzyx.sso.server.managers.CookieValueGenerator;
import com.bestzyx.sso.server.managers.SessionManager;
import com.bestzyx.sso.server.managers.TicketManager;
import com.bestzyx.sso.server.managers.UserManager;
import com.bestzyx.sso.server.token.User;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangyongxiang on 2021/6/11 10:23 上午
 **/
@Slf4j
@Service
public class TicketCacheManager implements TicketManager {
    
    private static Cache<String, CookieTicket> ticketManager;
    
    private final SimpleSSOProperties simpleSsoProperties;
    
    private final SessionManager sessionManager;
    
    private final UserManager userManager;
    
    private final Executor asyncExecutor = Executors.newWorkStealingPool();
    
    private final CookieValueGenerator cookieValueGenerator;
    
    @Autowired
    public TicketCacheManager(final SimpleSSOProperties simpleSsoProperties,
            final SessionManager sessionManager, final UserManager userManager,
            final CookieValueGenerator cookieValueGenerator) {
        this.simpleSsoProperties = simpleSsoProperties;
        ticketManager = Caffeine.newBuilder()
                .expireAfterAccess(
                        this.simpleSsoProperties.getTicketExpireTime())
                .expireAfterWrite(
                        this.simpleSsoProperties.getTicketExpireTime())
                .executor(asyncExecutor).recordStats().softValues()
                .removalListener((key, value, removalCause) -> log.info(
                        "session expire key: {}, value: {}, remove cause: {}",
                        key, value, removalCause))
                .scheduler(Scheduler.forScheduledExecutorService(
                        new ScheduledThreadPoolExecutor(
                                Runtime.getRuntime().availableProcessors())))
                .build();
        this.sessionManager = sessionManager;
        this.userManager = userManager;
        this.cookieValueGenerator = cookieValueGenerator;
    }
    
    @Override
    public CookieTicket createRefreshTicket(final String ssoCookie) {
        final String ticket = RandomStringUtils.randomAlphanumeric(16);
        final CookieTicket cookieTicket = CookieTicket.builder()
                .cookie(ssoCookie).ticket(ticket)
                .user(sessionManager.getUserToken(ssoCookie).getUser()).build();
        ticketManager.put(ticket, cookieTicket);
        return cookieTicket;
    }
    
    @Override
    public CookieTicket createNewTicket(final String username,
            final String password) {
        final User user = userManager.getUser(username, password);
        final String ticket = RandomStringUtils.randomAlphanumeric(16);
        
        final UsernamePasswordTicket cookieTicket = UsernamePasswordTicket
                .builder().username(username).password(password)
                .cookie(cookieValueGenerator.generateCookieValue(user))
                .ticket(ticket).user(user).build();
        ticketManager.put(ticket, cookieTicket);
        return cookieTicket;
    }
    
    @Override
    public CookieTicket validTicket(final String ticket) {
        return ticketManager.getIfPresent(ticket);
    }
}
