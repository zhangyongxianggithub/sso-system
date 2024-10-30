package com.bestzyx.sso.client.filters;

import java.io.IOException;
import java.util.Objects;

import org.springframework.web.filter.OncePerRequestFilter;

import com.bestzyx.sso.client.auth.SessionHolder;
import com.bestzyx.sso.client.config.AuthenticationProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static com.bestzyx.sso.client.constants.AuthenticationConstant.TICKET_PARAMETER_NAME;
import static java.util.Objects.nonNull;

/**
 * Created by zhangyongxiang on 2021/6/10 9:29 下午
 **/
@Slf4j
public class SimpleSSOLogoutFilter extends OncePerRequestFilter {
    
    private final AuthenticationProperties authenticationProperties;
    
    public SimpleSSOLogoutFilter(
            final AuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }
    
    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final FilterChain filterChain)
            throws ServletException, IOException {
        if (Objects.equals(authenticationProperties.getLogoutPath(),
                httpServletRequest.getRequestURI())
                && nonNull(
                        httpServletRequest.getParameter(TICKET_PARAMETER_NAME))
                && Objects.equals(
                        httpServletRequest.getParameter(TICKET_PARAMETER_NAME),
                        SessionHolder.getToken())) {
            SessionHolder.removeSession(SessionHolder.getToken()).invalidate();
            
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}
