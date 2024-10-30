package com.bestzyx.sso.client.utils;

/**
 * Created by zhangyongxiang on 2021/6/10 7:57 下午
 **/
public class JSONException extends RuntimeException {
    private static final long serialVersionUID = 8799416449659616836L;

    public JSONException() {}
    
    public JSONException(final String message) {
        super(message);
    }
    
    public JSONException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public JSONException(final Throwable cause) {
        super(cause);
    }
    
    public JSONException(final String message, final Throwable cause,
                         final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
