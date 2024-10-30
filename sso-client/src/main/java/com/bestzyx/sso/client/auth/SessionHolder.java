package com.bestzyx.sso.client.auth;

import java.util.Map;

import com.bestzyx.sso.client.principals.User;
import com.bestzyx.sso.client.principals.UserAuthenticationToken;
import com.bestzyx.sso.client.utils.SessionUtils;
import com.google.common.collect.Maps;

import jakarta.servlet.http.HttpSession;

import static java.util.Objects.nonNull;

/**
 * Created by zhangyongxiang on 2021/6/10 8:26 下午
 **/
public class SessionHolder {
    
    private static final Map<String, HttpSession> TOKEN_SESSION = Maps
            .newConcurrentMap();
    
    public static synchronized void addSession(final String token,
            final HttpSession session) {
        TOKEN_SESSION.put(token, session);
    }
    
    public static synchronized HttpSession removeSession(final String token) {
        return TOKEN_SESSION.remove(token);
    }
    
    public static synchronized User getUser() {
        return SessionUtils.getUser();
    }
    
    public static synchronized String getToken() {
        final UserAuthenticationToken authenticationToken = SessionUtils
                .getToken();
        if (nonNull(authenticationToken)) {
            return authenticationToken.getTicket();
        }
        return null;
    }
}
