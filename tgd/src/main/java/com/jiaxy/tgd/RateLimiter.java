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

    protected double maxToken;

    protected double availableToken;

    protected long startMicros;

    protected long nextGenTokenMicros;

    protected double stableIntervalTokenMicros;


    public void setRate(double tokenPerSecond){
        if (tokenPerSecond < 0 ){
            throw new IllegalArgumentException("tokenPerSecond must be positive.");
        }
        doSetRate(tokenPerSecond);
    }

    protected abstract void doSetRate(double tokenPerSecond);

    public abstract void syncAvailableToken(long nowMicros);

    public abstract double getToken(double requiredToken);

    public long duration(){
        return MICROSECONDS.convert(System.nanoTime(),NANOSECONDS) - startMicros;
    }


    public static Builder builder(){
        return new Builder();
    }

    static class Builder<T extends RateLimiter> {

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
            limiter.startMicros = MICROSECONDS.convert(System.nanoTime(),NANOSECONDS);
            limiter.setRate(tokenPerSecond);
            return limiter;
        }

        private FailFastTokenBucketLimiter buildFailFastTokenBucketLimiter(){
            FailFastTokenBucketLimiter limiter = new FailFastTokenBucketLimiter();
            limiter.startMicros = MICROSECONDS.convert(System.nanoTime(),NANOSECONDS);
            limiter.setRate(tokenPerSecond);
            return limiter;
        }
    }

    static enum RateLimiterType {

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
