package com.bestzyx.sso.client.filters;

import java.io.IOException;

import org.apache.http.auth.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bestzyx.sso.client.auth.DefaultRedirectUrlCreator;
import com.bestzyx.sso.client.auth.RedirectUrlCreator;
import com.bestzyx.sso.client.auth.SSOAuthenticator;
import com.bestzyx.sso.client.auth.SimpleSSOAuthenticator;
import com.bestzyx.sso.client.auth.UrlConverter;
import com.bestzyx.sso.client.auth.UserContextHolder;
import com.bestzyx.sso.client.config.AuthenticationProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static com.bestzyx.sso.client.constants.AuthenticationConstant.AND;
import static com.bestzyx.sso.client.constants.AuthenticationConstant.LOGIN_PATH;
import static com.bestzyx.sso.client.constants.AuthenticationConstant.QUESTION_MARK;
import static com.bestzyx.sso.client.constants.AuthenticationConstant.REDIRECT_URL;
import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Created by zhangyongxiang on 2021/6/10 4:18 下午
 **/
@Slf4j
public class SimpleSSOLoginFilter extends OncePerRequestFilter {
    
    private final AuthenticationProperties authenticationProperties;
    
    private final SSOAuthenticator ssoAuthenticator;
    
    private final RedirectUrlCreator redirectUrlCreator;
    
    public SimpleSSOLoginFilter(
            final AuthenticationProperties authenticationProperties,
            final String contextPath, final UrlConverter urlConverter) {
        this.authenticationProperties = authenticationProperties;
        redirectUrlCreator = new DefaultRedirectUrlCreator(
                authenticationProperties, contextPath, urlConverter);
        ssoAuthenticator = new SimpleSSOAuthenticator(authenticationProperties,
                redirectUrlCreator);
    }
    
    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final FilterChain filterChain)
            throws ServletException, IOException {
        if (ssoAuthenticator.shouldAuthenticate(httpServletRequest)) {
            try {
                final boolean authenticated = ssoAuthenticator
                        .authenticate(httpServletRequest, httpServletResponse);
                if (authenticated && !httpServletResponse.isCommitted()) {
                    filterChain.doFilter(httpServletRequest,
                            httpServletResponse);
                }
            } catch (final AuthenticationException e) {
                log.error("{} need authenticate",
                        httpServletRequest.getRequestURL(), e);
                redirectLogin(httpServletRequest, httpServletResponse);
            } finally {
                UserContextHolder.remove();
            }
        } else {
            log.info("{} is escaped from authentication",
                    httpServletRequest.getRequestURI());
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
    
    private void redirectLogin(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        final String loginPathPair = String.format("%s=%s", LOGIN_PATH,
                redirectUrlCreator.getLoginPath());
        
        final String originalUrl = String.format("%s=%s", REDIRECT_URL,
                redirectUrlCreator.getRedirectUrl(request));
        
        final String loginUrl = join(EMPTY,
                authenticationProperties.getEntryPoint(), QUESTION_MARK,
                loginPathPair, AND, originalUrl);
        if (log.isDebugEnabled()) {
            log.debug("login path parameter {}, original url {}, final url {}",
                    loginPathPair, originalUrl, loginUrl);
        }
        log.info(
                "access url need be authenticated, now redirect to sso server");
        response.sendRedirect(loginUrl);
    }
}
