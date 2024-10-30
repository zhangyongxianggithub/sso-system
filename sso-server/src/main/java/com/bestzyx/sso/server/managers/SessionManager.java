package com.bestzyx.sso.server.managers;

import com.bestzyx.sso.server.token.UserAuthenticationToken;

import jakarta.servlet.http.Cookie;

/**
 * Created by zhangyongxiang on 2021/6/11 10:28 上午
 **/
public interface SessionManager {
    
    UserAuthenticationToken getUserToken(String sessionName);
    
    UserAuthenticationToken addUserToken(String ticket,
            UserAuthenticationToken token);
    
    Cookie createSsoCookie(String cookieValue);
    
    UserAuthenticationToken invalidate(String sessionName);
    
}
