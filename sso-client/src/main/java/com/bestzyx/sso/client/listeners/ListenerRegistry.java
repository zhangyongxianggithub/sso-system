package com.bestzyx.sso.client.listeners;

import java.util.List;

import jakarta.servlet.http.HttpSessionListener;

/**
 * Created by zhangyongxiang on 2021/6/11 12:18 上午
 **/
public interface ListenerRegistry {
    
    void addListener(HttpSessionListener listener);
    
    List<HttpSessionListener> getAll();
}
