package com.bestzyx.sso.server.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bestzyx.sso.server.managers.SessionManager;
import com.bestzyx.sso.server.managers.TicketManager;
import com.bestzyx.sso.server.managers.UserManager;
import com.bestzyx.sso.server.managers.cache.CookieTicket;
import com.bestzyx.sso.server.token.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static com.bestzyx.sso.server.constant.AuthenticationConstant.AND;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.EQUAL;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.LOGIN_PATH_NAME;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.QUESTION_MARK;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.REDIRECT_URL_NAME;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.SSO_SERVER_COOKIE_NAME;
import static com.bestzyx.sso.server.constant.AuthenticationConstant.TICKET_PARAMETER_NAME;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by zhangyongxiang on 2021/6/11 2:07 上午
 **/
@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private TicketManager ticketManager;
    
    @Autowired
    private UserManager userManager;
    
    @GetMapping
    public String login(
            @RequestParam(REDIRECT_URL_NAME) final String redirectUrl,
            @RequestParam(LOGIN_PATH_NAME) final String loginPath,
            @CookieValue(name = SSO_SERVER_COOKIE_NAME,
                    required = false) final String ssoCookie,
            final HttpServletRequest request)
            throws UnsupportedEncodingException {
        log.info(
                "login entry point, redirect url {}, login path {}, sso cookie {}",
                redirectUrl, loginPath, ssoCookie);
        if (isNotEmpty(ssoCookie)
                && nonNull(sessionManager.getUserToken(ssoCookie))) {
            final CookieTicket newTicket = ticketManager
                    .createRefreshTicket(ssoCookie);
            return redirectServiceLogin(redirectUrl, loginPath, newTicket);
        } else {
            return redirectToLogin(redirectUrl, loginPath, request);
        }
    }
    
    @PostMapping
    public String login(
            @RequestParam(REDIRECT_URL_NAME) final String redirectUrl,
            @RequestParam(LOGIN_PATH_NAME) final String loginPath,
            @RequestParam("username") final String username,
            @RequestParam("password") final String password,
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws UnsupportedEncodingException {
        log.info(
                "login post, redirect url {},login path {},username {},password {}",
                redirectUrl, loginPath, username, password);
        final User user = userManager.getUser(username, password);
        if (isNull(user)) {
            log.error("username {} password: {} not valid", username, password);
            request.setAttribute("errorMessage",
                    "user not exists or password is wrong");
            return redirectToLogin(redirectUrl, loginPath, request);
        }
        final CookieTicket cookieTicket = ticketManager
                .createNewTicket(username, password);
        response.addCookie(
                sessionManager.createSsoCookie(cookieTicket.getCookie()));
        
        return redirectServiceLogin(redirectUrl, loginPath, cookieTicket);
    }
    
    private String redirectServiceLogin(final String redirectUrl,
            final String loginPath, final CookieTicket ticket)
            throws UnsupportedEncodingException {
        final String loginFilterUrl = getLoginFilterUrl(redirectUrl, loginPath);
        
        return StringUtils.join("redirect:", loginFilterUrl, QUESTION_MARK,
                TICKET_PARAMETER_NAME, EQUAL, ticket.getTicket(), AND,
                REDIRECT_URL_NAME, EQUAL, URLEncoder.encode(redirectUrl,
                        Charset.defaultCharset().name()));
    }
    
    private String redirectToLogin(final String redirectUrl,
            final String loginPath, final HttpServletRequest request) {
        request.setAttribute(REDIRECT_URL_NAME, redirectUrl);
        request.setAttribute(LOGIN_PATH_NAME, loginPath);
        return "/login";
    }
    
    private String getLoginFilterUrl(final String redirectUrl,
            final String loginPath) throws UnsupportedEncodingException {
        final String originalUrl = URLDecoder.decode(redirectUrl,
                Charset.defaultCharset().name());
        final URI originalUri = URI.create(originalUrl);
        return String.format("%s://%s:%s%s", originalUri.getScheme(),
                originalUri.getHost(), originalUri.getPort(), loginPath);
    }
}
