package com.bestzyx.sso.client.principals;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import static java.util.Objects.isNull;

/**
 * Created by zhangyongxiang on 2021/6/10 4:46 下午
 **/
@Data
public class UserAuthenticationToken implements Serializable {
    
    private static final long serialVersionUID = -864239575043951864L;

    /**
     * user info
     */
    private User user;

    /**
     * ticket,not useful
     */
    private String ticket;
    
    private Duration expireTime;
    
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @JsonIgnore
    public boolean isExpired() {
        return isNull(createdTime) || isNull(expireTime)
                || System.currentTimeMillis() > createdTime
                        .atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
                        + expireTime.toMillis();
    }
}
