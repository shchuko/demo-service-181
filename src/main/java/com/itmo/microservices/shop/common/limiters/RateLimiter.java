package com.itmo.microservices.shop.common.limiters;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class RateLimiter implements Limiter {
    private final Semaphore semaphore;
    private final int maxPermits;
    private final ScheduledExecutorService scheduler;

    public RateLimiter(int permits, TimeUnit timePeriod) {
        this.semaphore = new Semaphore(permits);
        this.maxPermits = permits;

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> semaphore.release(maxPermits - semaphore.availablePermits()), 1, 1, timePeriod);
    }

    @Override
    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
    }

}
