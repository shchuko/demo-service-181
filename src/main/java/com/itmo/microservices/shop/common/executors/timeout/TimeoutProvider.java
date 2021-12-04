package com.itmo.microservices.shop.common.executors.timeout;

public interface TimeoutProvider {
    boolean hasNextTimeout();

    long nextTimeoutMillis();


}
