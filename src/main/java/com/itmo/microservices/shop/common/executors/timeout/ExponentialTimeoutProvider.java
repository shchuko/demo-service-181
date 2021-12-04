package com.itmo.microservices.shop.common.executors.timeout;

import java.util.Random;

public class ExponentialTimeoutProvider implements TimeoutProvider {
    private static final double BACKOFF_FACTOR = 2;
    private static final double BACKOFF_JITTER = 0.1;

    private final Random random = new Random();
    private long effectiveTimeoutMs;
    private final long maxTimeoutMs;

    public ExponentialTimeoutProvider(long initialTimeoutMs) {
        this(initialTimeoutMs, Long.MAX_VALUE);
    }

    public ExponentialTimeoutProvider(long initialTimeoutMs, long maxTimeoutMs) {
        if (initialTimeoutMs > maxTimeoutMs) {
            throw new IllegalArgumentException("initialTimeoutMs {" + initialTimeoutMs +
                    "} should not be greater than maxTimeoutMs {" + maxTimeoutMs + "}");
        }
        this.effectiveTimeoutMs = initialTimeoutMs;
        this.maxTimeoutMs = maxTimeoutMs;
    }

    @Override
    public boolean hasNextTimeout() {
        return true;
    }

    @Override
    public long nextTimeoutMillis() {
        long currentTimeout = effectiveTimeoutMs;

        long nextTimeout = (long) Math.min(effectiveTimeoutMs * BACKOFF_FACTOR, maxTimeoutMs);
        long jitter = (long) (random.nextGaussian() * effectiveTimeoutMs * BACKOFF_JITTER);
        effectiveTimeoutMs = nextTimeout + jitter;

        return currentTimeout;
    }
}
