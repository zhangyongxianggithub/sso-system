package com.bestzyx.sso.client.listeners;

import com.bestzyx.sso.client.auth.SessionHolder;
import com.bestzyx.sso.client.principals.UserAuthenticationToken;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import static com.bestzyx.sso.client.utils.SessionUtils.SESSION_TOKEN_ID;
import static java.util.Objects.nonNull;

/**
 * Created by zhangyongxiang on 2021/6/11 11:13 上午
 **/
public class LocalHttpSessionListener implements HttpSessionListener {
    
    @Override
    public void sessionCreated(final HttpSessionEvent se) {
        
    }
    
    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        final Object token = se.getSession().getAttribute(SESSION_TOKEN_ID);
        if (nonNull(token)
                && nonNull(((UserAuthenticationToken) token).getTicket())) {
            SessionHolder.removeSession(
                    ((UserAuthenticationToken) token).getTicket());
        }
    }
}
