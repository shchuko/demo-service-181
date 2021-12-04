package com.itmo.microservices.shop.common.executors.timeout;

public class FixedTimeoutProvider implements TimeoutProvider {
    private final long timeout;

    public FixedTimeoutProvider(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean hasNextTimeout() {
        return true;
    }

    @Override
    public long nextTimeoutMillis() {
        return timeout;
    }
}
