package com.itmo.microservices.shop.common.limiters;

public interface Limiter {
    boolean tryAcquire();

    void stop();
}
