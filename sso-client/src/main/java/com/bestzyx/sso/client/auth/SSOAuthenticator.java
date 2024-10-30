package com.bestzyx.sso.client.auth;

import org.apache.http.auth.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Created by zhangyongxiang on 2021/6/10 4:28 下午
 **/
public interface SSOAuthenticator {
    
    boolean shouldAuthenticate(HttpServletRequest request);
    
    boolean authenticate(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException;
}
