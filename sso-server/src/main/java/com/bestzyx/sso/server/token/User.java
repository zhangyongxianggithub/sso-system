package com.bestzyx.sso.server.token;

import lombok.Builder;
import lombok.Data;

/**
 * Created by zhangyongxiang on 2021/6/10 4:48 下午
 **/
@Builder
@Data
public class User {
    
    private Integer id;
    
    private String username;
    
    private String name;
    
    private String email;
    
    private String pic;
    
    private String password;
}
