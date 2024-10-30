package com.bestzyx.sso.client.auth;

import java.util.Optional;

import com.bestzyx.sso.client.principals.UserAuthenticationToken;

/**
 * Created by zhangyongxiang on 2021/6/28 6:50 下午
 **/
public class UserContextHolder {
    
    private static ThreadLocal<UserAuthenticationToken> threadLocal = new ThreadLocal<>();
    
    public static void setContext(final UserAuthenticationToken token) {
        threadLocal.set(token);
    }
    
    public static Optional<UserAuthenticationToken> currentContext() {
        return Optional.ofNullable(threadLocal.get());
    }
    
    public static Optional<UserAuthenticationToken> remove() {
        final Optional<UserAuthenticationToken> token = currentContext();
        threadLocal.remove();
        return token;
    }
}
