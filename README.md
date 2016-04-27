TGD:三峡大坝（限流）
======================

实例
------
```java
        RateLimiter limiter = RateLimiter.builder().
                withTokenPerSecond(1).
                withType(RateLimiter.RateLimiterType.TB).
                build();
        for ( int i = 0 ;i < 10;i++){
            limiter.getToken(1);
            //...
        }

```