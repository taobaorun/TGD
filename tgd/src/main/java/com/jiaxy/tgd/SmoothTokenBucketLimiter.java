package com.jiaxy.tgd;

import java.util.concurrent.TimeUnit;

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
    public void syncAvailableToken(long nowMicros) {
        if (nowMicros > nextGenTokenMicros){
            double newTokens = (nowMicros - nextGenTokenMicros) / stableIntervalTokenMicros;
            availableToken = Math.min(maxToken,availableToken + newTokens);
            nextGenTokenMicros = nowMicros;
        }
    }

    @Override
    public double getToken(double requiredToken) {
        long waitMicros;
        long sleepTime;
        long oldNextGenTokenMicros;
        long nowMicros = duration();
        synchronized (mutex){
            syncAvailableToken(nowMicros);
            oldNextGenTokenMicros = nextGenTokenMicros;
            double tokenPermitted = Math.min(requiredToken,availableToken);
            double needNewToken = requiredToken - tokenPermitted;
            waitMicros = (long) (needNewToken * stableIntervalTokenMicros);
            nextGenTokenMicros =  nextGenTokenMicros + waitMicros;
            availableToken -= tokenPermitted;
        }
        sleepTime = Math.max( oldNextGenTokenMicros - nowMicros,0 );
        uninterruptibleSleep(sleepTime,MICROSECONDS);
        return sleepTime;
    }


    private void uninterruptibleSleep(long sleepTime,TimeUnit unit){
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(sleepTime);
            long end = System.nanoTime() + remainingNanos;
            while (true){
                try {
                    NANOSECONDS.sleep(remainingNanos);
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted){
                Thread.currentThread().interrupt();
            }
        }
    }
}
