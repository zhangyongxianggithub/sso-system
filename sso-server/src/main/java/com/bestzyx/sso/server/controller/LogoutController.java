package com.bestzyx.sso.server.controller;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bestzyx.sso.server.managers.SessionManager;
import com.bestzyx.sso.server.token.UserAuthenticationToken;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static com.bestzyx.sso.server.constant.AuthenticationConstant.EQUAL;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.QUESTION_MARK;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.SSO_SERVER_COOKIE_NAME;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.TICKET_PARAMETER_NAME;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Created by zhangyongxiang on 2021/6/12 7:17 下午
 **/
@Slf4j
@Controller
@RequestMapping("/logout")
public class LogoutController {
    
    @Autowired
    private SessionManager sessionManager;
    
    @GetMapping
    public ResponseEntity<Void> logout(
            @CookieValue(SSO_SERVER_COOKIE_NAME) final String ssoCookie,
            final HttpServletResponse response) {
        final UserAuthenticationToken token = sessionManager
                .invalidate(ssoCookie);
        invalidateCookie(response);
        invalidateClientToken(token);
        return ResponseEntity.ok().build();
    }
    
    private void invalidateCookie(final HttpServletResponse response) {
        final Cookie cookie = new Cookie(SSO_SERVER_COOKIE_NAME,
                SSO_SERVER_COOKIE_NAME);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    
    private void invalidateClientToken(final UserAuthenticationToken token) {
        if (nonNull(token) && !CollectionUtils.isEmpty(token.getLogoutUrls())) {
            CompletableFuture.allOf(
                    token.getLogoutUrls().stream().map(url -> (Runnable) () -> {
                        try {
                            Request.Get(url.concat(QUESTION_MARK)
                                    .concat(TICKET_PARAMETER_NAME).concat(EQUAL)
                                    .concat(token.getTicket())).execute();
                        } catch (final IOException e) {
                            log.error("{} execute logout failed", url, e);
                        }
                    }).map(CompletableFuture::runAsync)
                            .toArray(CompletableFuture[]::new))
                    .whenCompleteAsync((nullValue, exception) -> {
                        if (isNull(exception)) {
                            log.info("token {} logout", token);
                        } else {
                            log.error("token {} logout failed", token,
                                    exception);
                        }
                    });
        }
    }
}
