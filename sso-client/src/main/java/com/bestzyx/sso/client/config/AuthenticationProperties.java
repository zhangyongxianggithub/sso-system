package com.bestzyx.sso.client.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.bestzyx.sso.client.auth.UrlConverter;

import lombok.Data;

import static com.bestzyx.sso.client.constants.AuthenticationConstant.DEFAULT_LOGOUT_PATH;
import static com.bestzyx.sso.client.constants.AuthenticationConstant.DEFAULT_TICKET_LOGIN_PATH;

/**
 * Created by zhangyongxiang on 2021/6/10 4:09 下午
 **/
@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.sso")
public class AuthenticationProperties {
    /**
     * SSO login address
     */
    private String entryPoint;
    
    /**
     * a address for valid the ticket to generate token
     */
    private String ticketValidUrl;
    
    /**
     * login path for local service
     */
    private String loginPath = DEFAULT_TICKET_LOGIN_PATH;
    
    /**
     * logout path for local service
     */
    private String logoutPath = DEFAULT_LOGOUT_PATH;
    
    /**
     * include rule
     */
    private List<String> includePaths;
    
    /**
     * exclude rule
     */
    private List<String> excludePaths;
    
    /**
     * gateway entry point
     */
    private Gateway gateway = new Gateway();
    
    @Data
    public static class Gateway {
        
        /**
         * specify the gateway address(ip or domain) if access by gateway
         */
        private String address;
        
        /**
         * the gateway port
         */
        private int port = 80;
        
        /**
         * enable gateway
         */
        private boolean enabled = false;
        
        /**
         * url converter for gateway
         */
        private Class<UrlConverter> converterClass;
    }
}
