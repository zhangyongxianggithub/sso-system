package com.bestzyx.sso.client.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

import com.bestzyx.sso.client.config.AuthenticationProperties;
import com.bestzyx.sso.client.principals.UserAuthenticationToken;
import com.bestzyx.sso.client.utils.JsonUtils;
import com.bestzyx.sso.client.utils.SessionUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;

import static com.bestzyx.sso.client.constants.AuthenticationConstant.AND;
import static com.bestzyx.sso.client.constants.AuthenticationConstant.EQUAL;
import static com.bestzyx.sso.client.constants.AuthenticationConstant.LOGOUT_PATH;
import static com.bestzyx.sso.client.constants.AuthenticationConstant.QUESTION_MARK;
import static com.bestzyx.sso.client.constants.AuthenticationConstant.REDIRECT_URL;
import static com.bestzyx.sso.client.constants.AuthenticationConstant.TICKET_PARAMETER_NAME;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * Created by zhangyongxiang on 2021/6/10 5:18 下午
 **/
@Slf4j
public class DefaultTicketValidator implements TicketValidator {
    
    private final AuthenticationProperties authenticationProperties;
    
    private final RedirectUrlCreator redirectUrlCreator;
    
    public DefaultTicketValidator(
            final AuthenticationProperties authenticationProperties,
            final RedirectUrlCreator redirectUrlCreator) {
        this.authenticationProperties = authenticationProperties;
        this.redirectUrlCreator = redirectUrlCreator;
    }
    
    // TODO 后续改造通过FEIGN或者RestTemplate的方式访问服务
    @Override
    public UserAuthenticationToken valid(final String ticket) {
        final String ssoServerTicketValidUrl = authenticationProperties
                .getTicketValidUrl();
        if (log.isDebugEnabled()) {
            log.debug("ticket is {}, valid url is {}", ticket,
                    ssoServerTicketValidUrl);
        }
        String queryString = EMPTY;
        try {
            queryString = String.join(AND,
                    String.join(EQUAL, TICKET_PARAMETER_NAME, ticket),
                    String.join(EQUAL, LOGOUT_PATH,
                            redirectUrlCreator.getLogoutPath()),
                    String.join(EQUAL, REDIRECT_URL,
                            URLEncoder.encode(SessionUtils.getRedirectUrl(),
                                    Charset.defaultCharset().name())));
        } catch (final UnsupportedEncodingException e) {
            log.error("encode error, unsupported charset {}",
                    Charset.defaultCharset().name(), e);
        }
        try {
            final HttpResponse response = Request
                    .Get(ssoServerTicketValidUrl + QUESTION_MARK + queryString)
                    .execute().returnResponse();
            if (response.getStatusLine().getStatusCode() == SC_OK) {
                return JsonUtils.fromJson(
                        IOUtils.toString(response.getEntity().getContent()),
                        new TypeReference<UserAuthenticationToken>() {});
            } else {
                final int errorCode = response.getStatusLine().getStatusCode();
                final String message = IOUtils
                        .toString(response.getEntity().getContent());
                log.error(
                        "valid ticket {} error,error code is {}, message is {}",
                        ticket, errorCode, message);
                throw new TicketInvalidException(errorCode, message);
            }
        } catch (final IOException e) {
            log.error("valid ticket {} error", ticket, e);
            throw new TicketValidFailException(e);
        }
    }
}
