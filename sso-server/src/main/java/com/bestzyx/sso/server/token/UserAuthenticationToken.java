package com.bestzyx.sso.server.token;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import com.google.common.collect.Sets;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * Created by zhangyongxiang on 2021/6/10 4:46 下午
 **/
@Builder
@Data
public class UserAuthenticationToken implements Serializable {
    
    private static final long serialVersionUID = -864239575043951864L;
    
    private User user;
    
    private String ticket;
    
    private Duration expireTime;
    
    private LocalDateTime createdTime = LocalDateTime.now();
    
    private Set<String> logoutUrls = Sets.newHashSet();
    
    @Tolerate
    public UserAuthenticationToken() {}
}
