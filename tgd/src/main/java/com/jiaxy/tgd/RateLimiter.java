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
 * @since 2016/04/26 21:20
 */
public abstract class RateLimiter {

    protected double maxTokens;

    protected double availableTokens;

    protected long startNanos;

    protected long nextGenTokenMicros;

    protected double stableIntervalTokenMicros;

    protected final Object mutex = new Object();

    public void setRate(double tokenPerSecond){
        if (tokenPerSecond < 0 ){
            throw new IllegalArgumentException("tokenPerSecond must be positive.");
        }
        synchronized (mutex){
            doSetRate(tokenPerSecond);
        }
    }

    protected abstract void doSetRate(double tokenPerSecond);

    public abstract void syncAvailableToken(long nowMicros);

    public abstract double getToken(double requiredToken);

    public long duration(){
        return MICROSECONDS.convert(System.nanoTime() - startNanos,NANOSECONDS);
    }


    public static Builder builder(){
        return new Builder();
    }

    public static class Builder<T extends RateLimiter> {

        private double tokenPerSecond;

        private RateLimiterType type;

        public Builder withType(RateLimiterType type){
            this.type = type;
            return this;
        }

        public Builder withTokePerSecond(double tokenPerSecond){
            this.tokenPerSecond = tokenPerSecond;
            return this;
        }

        public T build(){
            switch (type){
                case TB:
                    return (T)buildSmoothTokenBucketLimiter();
                case LB:
                    return null;
                case FFTB:
                    return (T)buildFailFastTokenBucketLimiter();
                default:
                    return (T)buildSmoothTokenBucketLimiter();
            }
        }


        private SmoothTokenBucketLimiter buildSmoothTokenBucketLimiter(){
            SmoothTokenBucketLimiter limiter = new SmoothTokenBucketLimiter();
            limiter.startNanos = System.nanoTime();
            limiter.setRate(tokenPerSecond);
            return limiter;
        }

        private FailFastTokenBucketLimiter buildFailFastTokenBucketLimiter(){
            FailFastTokenBucketLimiter limiter = new FailFastTokenBucketLimiter();
            limiter.startNanos = System.nanoTime();
            limiter.setRate(tokenPerSecond);
            return limiter;
        }
    }

    public static enum RateLimiterType {

        /**
         * token bucket
         */
        TB,


        /**
         * leaky bucket
         */
        LB,


        /**
         * 没有可用token 抛出异常的token bucket
         */
        FFTB;
    }


}
