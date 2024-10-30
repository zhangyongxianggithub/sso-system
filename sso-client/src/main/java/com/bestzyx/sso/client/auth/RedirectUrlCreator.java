package com.bestzyx.sso.client.auth;

import java.io.UnsupportedEncodingException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Created by zhangyongxiang on 2021/6/12 5:40 下午
 **/

public interface RedirectUrlCreator {
    String getLoginPath() throws UnsupportedEncodingException;
    
    String getLogoutPath() throws UnsupportedEncodingException;
    
    String getRedirectUrl(HttpServletRequest request)
            throws UnsupportedEncodingException;
    
}
