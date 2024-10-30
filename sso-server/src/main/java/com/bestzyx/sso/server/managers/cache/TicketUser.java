package com.bestzyx.sso.server.managers.cache;

import com.bestzyx.sso.server.token.User;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Created by zhangyongxiang on 2021/6/12 9:26 上午
 **/
@Data
@SuperBuilder
public class TicketUser {
    
    /**
     * 票据
     */
    private String ticket;
    
    /**
     * 用户信息
     */
    private User user;
}
