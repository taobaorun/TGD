package com.jiaxy.tgd;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     没有可用token,抛出此异常
 * </p>
 * <br>
 *
 * @author <a href=mailto:taobaorun@gmail.com>taobaorun</a>
 *         <br>
 * @since 2016/04/26 22:40
 */
public class LimitedException extends RuntimeException{

    public LimitedException() {
    }

    public LimitedException(String message) {
        super(message);
    }

    public LimitedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LimitedException(Throwable cause) {
        super(cause);
    }

    public LimitedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
