package com.bestzyx.sso.server.managers.cache;

import org.springframework.stereotype.Service;

import com.bestzyx.sso.server.managers.CookieValueGenerator;
import com.bestzyx.sso.server.token.User;
import com.bestzyx.sso.server.utils.JsonUtils;

import static com.bestzyx.sso.server.constant.AuthenticationConstant.HYPHEN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.springframework.util.DigestUtils.md5DigestAsHex;

/**
 * Created by zhangyongxiang on 2021/6/12 10:23 上午
 **/
@Service
public class RandomCookieValueGenerator implements CookieValueGenerator {
    /**
     * how cookie generate
     * 
     * @param user user info
     * @return cookie value
     */
    
    private static final int RANDOM_SIZE = 5;
    
    @Override
    public String generateCookieValue(final User user) {
        
        final String userJson = JsonUtils.toJson(user);
        
        return md5DigestAsHex(userJson.getBytes(UTF_8)).concat(HYPHEN)
                .concat(randomAlphanumeric(RANDOM_SIZE)).concat(HYPHEN)
                .concat(user.getUsername());
    }
}
