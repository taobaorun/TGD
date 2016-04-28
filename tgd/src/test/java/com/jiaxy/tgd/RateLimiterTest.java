package com.jiaxy.tgd;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 * <br>
 *
 * @author <a href=mailto:taobaorun@gmail.com>taobaorun</a>
 *         <br>
 * @since 2016/04/26 22:19
 */
public class RateLimiterTest {


    @Test
    public void testTBGetToken() throws Exception {
        RateLimiter limiter = RateLimiter.builder().
                withTokePerSecond(10).
                withType(RateLimiter.RateLimiterType.TB).
                build();
        int i = 0;
        while (true) {
            try {
                limiter.getToken(1);
                System.out.println(i+".================="+format(new Date()));
                i++;
            } catch (Exception e) {
            }

            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }

    }

    @Test
    public void testFFTBGetToken() throws Exception {
        RateLimiter limiter = RateLimiter.builder().
                withTokePerSecond(10).
                withType(RateLimiter.RateLimiterType.FFTB).
                build();
//        Thread.sleep(4000);
        int i = 0;
        while (true) {
            try {
                limiter.getToken(1);
                System.out.println((i++)+".================="+format(new Date()));
            } catch (Exception e) {
                //System.out.println(e.getMessage());
            }

            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }
    }

    @Test
    public void testMultiFFBGetToken() throws Exception {
        final RateLimiter limiter = RateLimiter.builder().
                withTokePerSecond(10).
                withType(RateLimiter.RateLimiterType.FFTB).
                build();
        for ( int j = 0 ;j < 3;j++){
             new Thread(new Runnable() {
                public void run() {
                    int i = 0;
                    while (true) {
                        try {
                            limiter.getToken(1);
                            System.out.println(i+".================="+format(new Date()));
                            i++;
                        } catch (Exception e) {
                        }

                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                        }
                    }
                }
            }).start();
        }
        synchronized (RateLimiterTest.class){
            while (true){
                try {
                    RateLimiterTest.class.wait();
                } catch (Exception e){

                }
            }
        }
    }

    private String format(Date date){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        return sf.format(date);
    }
}
