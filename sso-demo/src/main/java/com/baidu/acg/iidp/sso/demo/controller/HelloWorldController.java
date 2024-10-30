package com.baidu.acg.iidp.sso.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bestzyx.sso.client.SimpleSSOAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangyongxiang on 2021/6/11 4:13 下午
 **/
@Slf4j
@RestController
public class HelloWorldController {

    @Autowired
    private SimpleSSOAutoConfiguration simpleSSOAutoConfiguration;
    
    @GetMapping
    public Map<String, String> get(@RequestParam("test") final String test) {
        final Map map = new HashMap();

        map.put("hello", "world");
        return map;
    }
}
