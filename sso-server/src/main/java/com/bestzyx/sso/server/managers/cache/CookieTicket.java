package com.bestzyx.sso.server.managers.cache;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Created by zhangyongxiang on 2021/6/11 1:57 下午
 **/
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public class CookieTicket extends TicketUser {
    
    /**
     * 重新刷新生成的cookie
     */
    private String cookie;

}
