package com.bestzyx.sso.client.auth;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;

import com.bestzyx.sso.client.config.AuthenticationProperties;

import jakarta.servlet.http.HttpServletRequest;

import static com.bestzyx.sso.client.constants.AuthenticationConstant.QUESTION_MARK;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.appendIfMissing;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.apache.commons.lang3.StringUtils.prependIfMissing;

/**
 * Created by zhangyongxiang on 2021/6/12 5:40 下午
 **/
public class DefaultRedirectUrlCreator implements RedirectUrlCreator {
    
    private final AuthenticationProperties authenticationProperties;
    
    private final String contextPath;
    
    private final UrlConverter urlConverter;
    
    public DefaultRedirectUrlCreator(
            final AuthenticationProperties authenticationProperties,
            final String contextPath, final UrlConverter urlConverter) {
        this.authenticationProperties = authenticationProperties;
        this.contextPath = defaultIfBlank(contextPath, EMPTY);
        this.urlConverter = urlConverter;
    }
    
    /**
     * 人工指定一个以网关或者当前服务的路径与redirect的host与port拼接成一个合法的login路径
     * 
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public String getLoginPath() throws UnsupportedEncodingException {
        String loginPath = URLEncoder.encode(
                authenticationProperties.getLoginPath(),
                Charset.defaultCharset().name());
        if (isNoneBlank(contextPath)) {
            loginPath = appendIfMissing(loginPath, contextPath);
        }
        if (authenticationProperties.getGateway().isEnabled()
                && nonNull(urlConverter)) {
            loginPath = urlConverter.convert(loginPath);
        }
        return loginPath;
    }
    
    @Override
    public String getLogoutPath() throws UnsupportedEncodingException {
        String logoutPath = URLEncoder.encode(
                authenticationProperties.getLogoutPath(),
                Charset.defaultCharset().name());
        if (isNoneBlank(contextPath)) {
            logoutPath = appendIfMissing(logoutPath, contextPath);
        }
        if (authenticationProperties.getGateway().isEnabled()
                && nonNull(urlConverter)) {
            logoutPath = urlConverter.convert(logoutPath);
        }
        return logoutPath;
    }
    
    @Override
    public String getRedirectUrl(final HttpServletRequest request)
            throws UnsupportedEncodingException {
        final AuthenticationProperties.Gateway gateway = authenticationProperties
                .getGateway();
        String redirectUrl = request.getRequestURL().toString();
        if (gateway.isEnabled()) {
            String redirectPath = request.getRequestURI();
            if (nonNull(urlConverter)) {
                redirectPath = urlConverter.convert(redirectPath);
            }
            final URI uri = URI.create(request.getRequestURL().toString());
            redirectUrl = String.format("%s://%s:%s", request.getScheme(),
                    StringUtils.defaultIfBlank(gateway.getAddress(),
                            uri.getHost()),
                    isNoneBlank(gateway.getAddress()) ? gateway.getPort()
                            : uri.getPort())
                    .concat(redirectPath);
        }
        return URLEncoder.encode(
                redirectUrl.concat(defaultIfBlank(prependIfMissing(
                        request.getQueryString(), QUESTION_MARK), EMPTY)),
                Charset.defaultCharset().name());
    }
}
