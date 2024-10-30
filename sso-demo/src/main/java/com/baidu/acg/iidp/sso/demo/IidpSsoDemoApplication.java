package com.baidu.acg.iidp.sso.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class IidpSsoDemoApplication {
    
    public static void main(final String[] args) {
        final ConfigurableApplicationContext cac = SpringApplication
                .run(IidpSsoDemoApplication.class, args);
    }
    
}
