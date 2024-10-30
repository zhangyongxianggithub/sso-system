package com.bestzyx.sso.server.controller;

import java.net.URI;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bestzyx.sso.server.config.SimpleSSOProperties;
import com.bestzyx.sso.server.managers.SessionManager;
import com.bestzyx.sso.server.managers.TicketManager;
import com.bestzyx.sso.server.managers.cache.CookieTicket;
import com.bestzyx.sso.server.token.UserAuthenticationToken;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

import static com.bestzyx.sso.server.constant.AuthenticationConstant.LOGOUT_PATH_NAME;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.REDIRECT_URL_NAME;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.TICKET_PARAMETER_NAME;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Created by zhangyongxiang on 2021/6/11 3:47 下午
 **/
@Slf4j
@RestController
@RequestMapping("/tgt")
public class TicketController {
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private TicketManager ticketManager;
    
    @Autowired
    private SimpleSSOProperties simpleSsoProperties;
    
    @GetMapping
    public ResponseEntity<UserAuthenticationToken> valid(
            @RequestParam(TICKET_PARAMETER_NAME) final String ticket,
            @RequestParam(LOGOUT_PATH_NAME) final String logoutPath,
            @RequestParam(REDIRECT_URL_NAME) final String redirectUrl) {
        
        final CookieTicket cookieTicket = ticketManager.validTicket(ticket);
        if (nonNull(cookieTicket)) {
            log.info("{} is a valid ticket", ticket);
            final String logoutUrl = getLogoutUrl(redirectUrl, logoutPath);
            log.info("logout url from redirect url & path {}", logoutUrl);
            UserAuthenticationToken token = sessionManager
                    .getUserToken(cookieTicket.getCookie());
            if (isNull(token)) {
                token = UserAuthenticationToken.builder()
                        .logoutUrls(Sets.newHashSet())
                        .user(cookieTicket.getUser()).ticket(ticket)
                        .createdTime(LocalDateTime.now())
                        .expireTime(simpleSsoProperties.getTokenExpireTime())
                        .build();
            }
            token.getLogoutUrls().add(logoutUrl);
            sessionManager.addUserToken(cookieTicket.getCookie(), token);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(UNAUTHORIZED).build();
    }
    
    private String getLogoutUrl(final String redirectUrl,
            final String logoutPath) {
        final URI uri = URI.create(redirectUrl);
        return String.format("%s://%s:%s%s", uri.getScheme(), uri.getHost(),
                uri.getPort(), logoutPath);
    }
    
}
