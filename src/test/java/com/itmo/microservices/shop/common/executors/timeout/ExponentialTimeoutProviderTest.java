package com.itmo.microservices.shop.common.executors.timeout;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExponentialTimeoutProviderTest {
    // TODO 1: exponential grow test
    // TODO 2: grow not exceeds max value test

    @Test
    void testReturnsCorrectInitialTimeout() {
        long expected = 42;
        TimeoutProvider provider = new ExponentialTimeoutProvider(expected);
        assertTrue(provider.hasNextTimeout());
        assertEquals(expected, provider.nextTimeoutMillis());
    }

    @Test
    void testThrowsCorrectInitialTimeout() {
        assertThrows(IllegalArgumentException.class, () -> {
            // 42 > 40 - illegal args
            new ExponentialTimeoutProvider(42, 40);
        });
    }

    @Test
    void testTimeoutGrows() {
        long initial = 42;
        TimeoutProvider provider = new ExponentialTimeoutProvider(initial);
        for (int i = 0; i < 50; ++i) {
            assertTrue(provider.hasNextTimeout());
            provider.nextTimeoutMillis();
        }

        assertThat(provider.nextTimeoutMillis()).isGreaterThan(initial);
    }
}