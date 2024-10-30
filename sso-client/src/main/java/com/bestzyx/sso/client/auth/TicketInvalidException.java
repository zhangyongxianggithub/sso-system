package com.bestzyx.sso.client.auth;

import lombok.Data;

/**
 * Created by zhangyongxiang on 2021/6/10 8:02 下午
 **/
@Data
public class TicketInvalidException extends RuntimeException {
    private static final long serialVersionUID = -6588679659982064957L;
    
    private int errorCode;
    
    private String message;
    
    public TicketInvalidException(final int errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
    
    public TicketInvalidException(final String message, final int errorCode,
                                  final String message1) {
        super(message);
        this.errorCode = errorCode;
        this.message = message1;
    }
    
    public TicketInvalidException(final String message, final Throwable cause,
                                  final int errorCode, final String message1) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message1;
    }
    
    public TicketInvalidException(final Throwable cause, final int errorCode,
                                  final String message) {
        super(cause);
        this.errorCode = errorCode;
        this.message = message;
    }
    
    public TicketInvalidException(final String message, final Throwable cause,
                                  final boolean enableSuppression, final boolean writableStackTrace,
                                  final int errorCode, final String message1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
        this.message = message1;
    }
}
