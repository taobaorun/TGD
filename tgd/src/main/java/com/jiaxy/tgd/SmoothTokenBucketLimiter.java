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
public class SmoothTokenBucketLimiter extends AbstractTokenBucketLimiter {

    @Override
    public double getToken(double requiredToken) {
        long waitMicros;
        long sleepTime;
        long oldNextGenTokenMicros;
        long nowMicros = duration();
        synchronized (mutex){
            syncAvailableToken(nowMicros);
            oldNextGenTokenMicros = nextGenTokenMicros;
            double tokenPermitted = Math.min(requiredToken, availableTokens);
            double needNewToken = requiredToken - tokenPermitted;
            waitMicros = (long) (needNewToken * stableIntervalTokenMicros);
            nextGenTokenMicros =  nextGenTokenMicros + waitMicros;
            availableTokens -= tokenPermitted;
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
