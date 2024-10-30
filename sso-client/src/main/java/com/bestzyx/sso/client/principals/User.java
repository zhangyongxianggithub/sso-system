package com.bestzyx.sso.client.principals;

import lombok.Data;

/**
 * Created by zhangyongxiang on 2021/6/10 4:48 下午
 **/
@Data
public class User {

    private Integer id;

    private String username;

    private String name;

    private String email;

    private String pic;
}
