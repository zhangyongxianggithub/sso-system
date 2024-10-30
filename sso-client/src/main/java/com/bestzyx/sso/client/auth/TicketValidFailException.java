package com.bestzyx.sso.client.auth;

/**
 * Created by zhangyongxiang on 2021/6/10 8:08 下午
 **/
public class TicketValidFailException extends RuntimeException {
    private static final long serialVersionUID = 7329636166237273400L;
    
    public TicketValidFailException() {}
    
    public TicketValidFailException(final String message) {
        super(message);
    }
    
    public TicketValidFailException(final String message,
            final Throwable cause) {
        super(message, cause);
    }
    
    public TicketValidFailException(final Throwable cause) {
        super(cause);
    }
    
    public TicketValidFailException(final String message, final Throwable cause,
            final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
