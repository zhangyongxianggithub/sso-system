package com.bestzyx.sso.client.constants;

/**
 * Created by zhangyongxiang on 2021/6/10 4:22 下午
 **/
public final class AuthenticationConstant {
    private AuthenticationConstant() {}
    
    public static final String DEFAULT_TICKET_LOGIN_PATH = "/sso/login";
    
    public static final String DEFAULT_LOGOUT_PATH = "/sso/logout";
    
    public static final String TICKET_PARAMETER_NAME = "ticket";
    
    public static final String REDIRECT_URL = "redirect-url";
    
    public static final String LOGIN_PATH = "login-path";

    public static final String LOGOUT_PATH = "logout-path";
    
    public static final String QUESTION_MARK = "?";
    
    public static final String EQUAL = "=";
    
    public static final String AND = "&";
}
