package com.jiaxy.tgd;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 * <br>
 *
 * @author <a href=mailto:taobaorun@gmail.com>taobaorun</a>
 *         <br>
 * @since 2016/04/26 21:31
 */
public class FailFastTokenBucketLimiter extends AbstractTokenBucketLimiter {

    private final Object mutex = new Object();

    @Override
    public double getToken(double requiredToken) {
        long nowMicros = duration();
        synchronized (mutex){
            syncAvailableToken(nowMicros);
            double tokenPermitted = Math.min(requiredToken, availableTokens);
            double needNewToken = requiredToken - tokenPermitted;
            if (needNewToken > 0){
                throw new LimitedException("no token.needNewToken:"+needNewToken+",tokenPermitted:"+tokenPermitted);
            }
            availableTokens -= tokenPermitted;
        }
        return 0;
    }
}
