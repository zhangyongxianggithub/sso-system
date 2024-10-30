package com.bestzyx.sso.server.managers.cache;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bestzyx.sso.server.managers.UserManager;
import com.bestzyx.sso.server.token.User;
import com.google.common.collect.Maps;

import static com.bestzyx.sso.server.constant.AuthenticationConstant.COLON;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.Base64Utils.encodeToUrlSafeString;

/**
 * Created by zhangyongxiang on 2021/6/11 2:59 下午
 **/
@Service
public class UserCacheManager implements UserManager {
    
    private static Map<String, User> users = Maps.newConcurrentMap();
    // TODO, 存储到数据库
    static {
        users.put(
                encodeToUrlSafeString("zhangyongxiang:123456".getBytes(UTF_8)),
                User.builder().id(1).email("zhangyongxiang@baidu.com")
                        .name("张永祥").username("zhangyongxiang")
                        .password("123456").build());
        users.put(encodeToUrlSafeString("admin:123456".getBytes(UTF_8)),
                User.builder().id(1).email("zhangyongxiang@baidu.com")
                        .name("张永祥").username("admin").password("123456")
                        .build());
    }
    
    @Override
    public User getUser(final String username, final String password) {
        return users.getOrDefault(
                encodeToUrlSafeString(StringUtils
                        .join(username, COLON, password).getBytes(UTF_8)),
                null);
    }
}
