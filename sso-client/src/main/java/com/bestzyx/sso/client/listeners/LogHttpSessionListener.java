package com.bestzyx.sso.client.listeners;


import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangyongxiang on 2021/6/11 12:16 上午
 **/
@Slf4j
public class LogHttpSessionListener implements HttpSessionListener {
    
    @Override
    public void sessionCreated(final HttpSessionEvent event) {
        log.info("session {} created, {}", event.getSession().getId(),
                event.getSession());
    }
    
    @Override
    public void sessionDestroyed(final HttpSessionEvent event) {
        log.info("{} will be destroyed", event.getSession());
    }
}
