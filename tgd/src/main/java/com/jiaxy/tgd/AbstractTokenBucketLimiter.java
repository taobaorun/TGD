package com.jiaxy.tgd;


import static java.util.concurrent.TimeUnit.SECONDS;
/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>taobaorun</a>
 *
 * @since 2016/04/27 10:46
 */
public abstract class AbstractTokenBucketLimiter extends RateLimiter {

    @Override
    protected void doSetRate(double tokenPerSecond) {
        syncAvailableToken(duration());
        this.maxTokens = tokenPerSecond;
        this.stableIntervalTokenMicros = SECONDS.toMicros(1L) / tokenPerSecond;
    }

    @Override
    public void syncAvailableToken(long nowMicros) {
        if (nowMicros > nextGenTokenMicros){
            double newTokens = (nowMicros - nextGenTokenMicros) / stableIntervalTokenMicros;
            availableTokens = Math.min(maxTokens, availableTokens + newTokens);
            nextGenTokenMicros = nowMicros;
        }
    }
}
