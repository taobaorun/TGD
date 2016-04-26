package com.jiaxy.tgd;

import static java.util.concurrent.TimeUnit.*;

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
public class SmoothTokenBucketLimiter extends RateLimiter {

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
            double newTokens = (nowMicros - nextGenTokenMicros) / stableIntervalTokenMicros;
            availableToken = Math.min(maxToken,availableToken + newTokens);
            nextGenTokenMicros = nowMicros;
        }
    }

    @Override
    public double getToken(double requiredToken) {

        double timeToWait;
        double sleepTime;
        double oldNextGenTokenMicros;
        double nowMicros = duration();
        synchronized (mutex){
            syncAvailableToken(nowMicros);
            oldNextGenTokenMicros = nextGenTokenMicros;
            double tokenPermitted = Math.min(requiredToken,availableToken);
            double needNewToken = requiredToken - tokenPermitted;
            timeToWait = needNewToken * stableIntervalTokenMicros;
            nextGenTokenMicros =  nextGenTokenMicros + timeToWait;
            availableToken -= tokenPermitted;
        }
        sleepTime = Math.max( oldNextGenTokenMicros - nowMicros,0 );
        try {
            MICROSECONDS.sleep((long)sleepTime);
        } catch (InterruptedException e) {
        }
        return 0;
    }
}
