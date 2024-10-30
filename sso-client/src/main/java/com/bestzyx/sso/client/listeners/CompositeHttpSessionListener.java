package com.bestzyx.sso.client.listeners;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Lists;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.nonNull;

/**
 * Created by zhangyongxiang on 2021/6/11 12:17 上午
 **/
@Slf4j
public class CompositeHttpSessionListener
        implements HttpSessionListener, ListenerRegistry {
    
    private final List<HttpSessionListener> listeners = Lists.newArrayList();
    
    @Override
    public void sessionCreated(final HttpSessionEvent se) {
        CompletableFuture
                .allOf(listeners.stream()
                        .map(listener -> (Runnable) () -> listener
                                .sessionCreated(se))
                        .map(CompletableFuture::runAsync)
                        .toArray(CompletableFuture[]::new))
                .whenCompleteAsync((nullValue, throwable) -> {
                    log.info("all http session listener complete");
                    if (nonNull(throwable)) {
                        log.error("http session listener run error", throwable);
                    }
                });
    }
    
    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        CompletableFuture
                .allOf(listeners.stream()
                        .map(listener -> (Runnable) () -> listener
                                .sessionDestroyed(se))
                        .map(CompletableFuture::runAsync)
                        .toArray(CompletableFuture[]::new))
                .whenCompleteAsync((nullValue, throwable) -> {
                    log.info("all http session listener complete");
                    if (nonNull(throwable)) {
                        log.error("http session listener run error", throwable);
                    }
                });
    }
    
    @Override
    public void addListener(final HttpSessionListener listener) {
        if (nonNull(listener)) {
            listeners.add(listener);
        }
    }
    
    @Override
    public List<HttpSessionListener> getAll() {
        return Collections.unmodifiableList(listeners);
    }
}
