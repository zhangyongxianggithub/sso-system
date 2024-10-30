package com.bestzyx.sso.server.managers;

import com.bestzyx.sso.server.token.User;

/**
 * Created by zhangyongxiang on 2021/6/11 2:58 下午
 **/
public interface UserManager {
    
    User getUser(String username, String password);
}
