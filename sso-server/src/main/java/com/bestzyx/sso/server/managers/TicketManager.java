package com.bestzyx.sso.server.managers;

import com.bestzyx.sso.server.managers.cache.CookieTicket;

/**
 * Created by zhangyongxiang on 2021/6/11 10:23 上午
 **/
public interface TicketManager {
    
    CookieTicket createRefreshTicket(String ssoCookie);
    
    CookieTicket createNewTicket(String username, String password);
    
    CookieTicket validTicket(String ticket);
}
