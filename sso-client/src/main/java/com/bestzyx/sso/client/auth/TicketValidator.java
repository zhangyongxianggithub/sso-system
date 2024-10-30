package com.bestzyx.sso.client.auth;

import com.bestzyx.sso.client.principals.UserAuthenticationToken;

/**
 * Created by zhangyongxiang on 2021/6/10 5:17 下午
 **/
public interface TicketValidator {
    
    UserAuthenticationToken valid(String ticket);
}
