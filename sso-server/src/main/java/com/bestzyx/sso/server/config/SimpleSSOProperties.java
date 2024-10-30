package com.bestzyx.sso.server.config;

import java.time.Duration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangyongxiang on 2021/6/11 10:52 上午
 **/
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "spring.simple-sso")
public class SimpleSSOProperties implements InitializingBean {
    
    /**
     * 服务器证书保留时间
     */
    private Duration sessionExpireTime = Duration.ofDays(7);
    
    /**
     * cookie保留时间，通常与证书保留时间一致
     */
    private Duration cookieExpireTime = Duration.ofDays(3);
    
    /**
     * ticket门票保留时间，通常要设的较短一些
     */
    private Duration ticketExpireTime = Duration.ofMinutes(10);
    
    /**
     * 这个服务的token有效时长
     */
    private Duration tokenExpireTime = Duration.ofDays(1);
    
    @Override
    public void afterPropertiesSet() {
        log.info(
                "session expire time: {},cookie expire time: {}, "
                        + "ticket expire time: {},token expire time: {}",
                sessionExpireTime, cookieExpireTime, ticketExpireTime,
                tokenExpireTime);
    }
}
