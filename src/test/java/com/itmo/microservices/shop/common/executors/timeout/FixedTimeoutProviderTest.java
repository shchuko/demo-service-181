package com.itmo.microservices.shop.common.executors.timeout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FixedTimeoutProviderTest {
    @Test
    void testReturnsCorrectTimeoutValue() {
        long expected = 42;
        TimeoutProvider provider = new FixedTimeoutProvider(expected);
        assertTrue(provider.hasNextTimeout());
        assertEquals(expected, provider.nextTimeoutMillis());
    }

    @Test
    void testAlwaysReturnsFixedValue() {
        long expected = 42;
        TimeoutProvider provider = new FixedTimeoutProvider(expected);

        for (int i = 0; i < 100; i++) {
            assertTrue(provider.hasNextTimeout(), "hasNextTimeout() returned false on " + i + " attempt");
            assertEquals(expected, provider.nextTimeoutMillis(), "nextTimeoutMillis() returned wrong value on " + i + " attempt");
        }
    }
}