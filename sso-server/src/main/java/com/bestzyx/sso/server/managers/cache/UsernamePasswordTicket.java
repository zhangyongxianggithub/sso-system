package com.bestzyx.sso.server.managers.cache;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Created by zhangyongxiang on 2021/6/11 1:57 下午
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class UsernamePasswordTicket extends CookieTicket {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
}
