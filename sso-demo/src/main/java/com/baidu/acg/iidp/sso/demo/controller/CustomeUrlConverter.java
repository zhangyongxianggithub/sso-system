package com.baidu.acg.iidp.sso.demo.controller;

import com.bestzyx.sso.client.auth.UrlConverter;

/**
 * Created by zhangyongxiang on 2021/6/12 4:59 下午
 **/
public class CustomeUrlConverter implements UrlConverter {
    @Override
    public String convert(final String path) {
        return path;
    }
}
