package com.example.hz.sso.core.exception;

/**
 * 2022年3月29日
 */
public class HzSsoException extends RuntimeException {

    public static final long servialVersionUID = 97L;

    public HzSsoException(String msg) {
        super(msg);
    }

    public HzSsoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public HzSsoException(Throwable cause) {
        super(cause);
    }
}
