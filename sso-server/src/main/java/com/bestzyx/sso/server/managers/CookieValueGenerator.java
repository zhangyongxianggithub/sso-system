package com.bestzyx.sso.server.managers;

import com.bestzyx.sso.server.token.User;

/**
 * Created by zhangyongxiang on 2021/6/12 10:23 上午
 **/
public interface CookieValueGenerator {

    String generateCookieValue(User user);
}
