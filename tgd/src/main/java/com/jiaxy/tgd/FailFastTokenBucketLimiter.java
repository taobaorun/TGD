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
public class FailFastTokenBucketLimiter extends RateLimiter {

    private final Object mutex = new Object();

    @Override
    protected void doSetRate(double tokenPerSecond) {
        syncAvailableToken(duration());
        this.maxToken = tokenPerSecond;
        this.stableIntervalTokenMicros = SECONDS.toMicros(1L) / tokenPerSecond;
    }

    @Override
    public void syncAvailableToken(double nowMicros) {
        if (nowMicros > nextGenTokenMicros){
            System.out.println("-----nowMicros---"+nowMicros);
            System.out.println("-----next---"+nextGenTokenMicros);
            double newTokens = (nowMicros - nextGenTokenMicros) / stableIntervalTokenMicros;
            availableToken = Math.min(maxToken,availableToken + newTokens);
            nextGenTokenMicros = nowMicros;
        }
    }

    @Override
    public double getToken(double requiredToken) {
        double nowMicros = duration();
        synchronized (mutex){
            syncAvailableToken(nowMicros);
            double tokenPermitted = Math.min(requiredToken,availableToken);
            double needNewToken = requiredToken - tokenPermitted;
            if (needNewToken > 0){
                throw new LimitedException("no token.needNewToken:"+needNewToken+",tokenPermitted:"+tokenPermitted);
            }
            availableToken -= tokenPermitted;
        }
        return 0;
    }
}
