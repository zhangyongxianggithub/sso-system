package com.bestzyx.sso.client.auth;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.http.auth.AuthenticationException;
import org.springframework.util.AntPathMatcher;

import com.bestzyx.sso.client.config.AuthenticationProperties;
import com.bestzyx.sso.client.principals.UserAuthenticationToken;
import com.google.common.collect.Lists;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static com.bestzyx.sso.client.constants.AuthenticationConstant.REDIRECT_URL;
import static com.bestzyx.sso.client.constants.AuthenticationConstant.TICKET_PARAMETER_NAME;
import static com.bestzyx.sso.client.utils.SessionUtils.getToken;
import static com.bestzyx.sso.client.utils.SessionUtils.setToken;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Created by zhangyongxiang on 2021/6/10 4:29 下午
 **/
@Slf4j
public class SimpleSSOAuthenticator implements SSOAuthenticator {
    
    private final AuthenticationProperties authenticationProperties;
    
    private final TicketValidator ticketValidator;
    
    private final RedirectUrlCreator redirectUrlCreator;
    
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    
    public SimpleSSOAuthenticator(
            final AuthenticationProperties authenticationProperties,
            final RedirectUrlCreator redirectUrlCreator) {
        this.authenticationProperties = authenticationProperties;
        ticketValidator = new DefaultTicketValidator(authenticationProperties,
                redirectUrlCreator);
        this.redirectUrlCreator = redirectUrlCreator;
    }
    
    @Override
    public boolean shouldAuthenticate(final HttpServletRequest request) {
        final String uri = request.getRequestURI();
        List<String> excludePaths = authenticationProperties.getExcludePaths();
        if (isNull(excludePaths)) {
            excludePaths = Lists.newLinkedList();
        }
        return !excludePaths.stream()
                .anyMatch(path -> antPathMatcher.match(path, uri));
    }
    
    @Override
    public boolean authenticate(final HttpServletRequest request,
            final HttpServletResponse response) throws AuthenticationException {
        final UserAuthenticationToken token = getToken();
        if (nonNull(token) && !token.isExpired()) {
            UserContextHolder.setContext(token);
            return true;
        }
        if (Objects.equals(request.getRequestURI(),
                authenticationProperties.getLoginPath())
                && nonNull(request.getParameter(TICKET_PARAMETER_NAME))) {
            final String ticket = request.getParameter(TICKET_PARAMETER_NAME);
            final UserAuthenticationToken newToken = ticketValidator
                    .valid(ticket);
            setToken(newToken);
            redirectOriginalUrl(request, response);
            return true;
            
        }
        throw new AuthenticationException("current url "
                + request.getRequestURL() + " authenticate failed");
    }
    
    private void redirectOriginalUrl(final HttpServletRequest request,
            final HttpServletResponse response) {
        final String originalUrl = Optional
                .ofNullable(request.getParameter(REDIRECT_URL))
                .orElseThrow(() -> new IllegalArgumentException(
                        "the request from sso or client not contain the redirect url"));
        try {
            response.sendRedirect(originalUrl);
        } catch (final IOException e) {
            throw new IllegalStateException("redirect to original url failed",
                    e);
        }
    }
}
