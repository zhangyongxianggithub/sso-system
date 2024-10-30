package com.bestzyx.sso.client.utils;

import java.util.Optional;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bestzyx.sso.client.auth.SessionHolder;
import com.bestzyx.sso.client.principals.User;
import com.bestzyx.sso.client.principals.UserAuthenticationToken;

import jakarta.servlet.http.HttpServletRequest;

import static com.bestzyx.sso.client.constants.AuthenticationConstant.REDIRECT_URL;
import static java.util.Objects.nonNull;

/**
 * Created by zhangyongxiang on 2021/6/10 4:52 下午
 **/
public final class SessionUtils {
    
    public static final String SESSION_TOKEN_ID = "sso-session-authentication-id";
    
    private SessionUtils() {}
    
    public static UserAuthenticationToken getToken() {
        return (UserAuthenticationToken) Optional.ofNullable(
                getRequest().getSession(true).getAttribute(SESSION_TOKEN_ID))
                .orElse(null);
    }
    
    public static User getUser() {
        return Optional.ofNullable(getToken())
                .map(UserAuthenticationToken::getUser).orElse(null);
    }
    
    public static void setToken(final UserAuthenticationToken token) {
        getRequest().getSession().setAttribute(SESSION_TOKEN_ID, token);
        SessionHolder.addSession(token.getTicket(), getRequest().getSession());
    }
    
    public static String getRedirectUrl() {
        return getRequest().getParameter(REDIRECT_URL);
    }
    
    private static HttpServletRequest getRequest() {
        final RequestAttributes requestAttributes = RequestContextHolder
                .currentRequestAttributes();
        if (nonNull(requestAttributes) && requestAttributes.getClass()
                .isAssignableFrom(ServletRequestAttributes.class)) {
            return ServletRequestAttributes.class.cast(requestAttributes)
                    .getRequest();
        }
        return null;
    }
}
