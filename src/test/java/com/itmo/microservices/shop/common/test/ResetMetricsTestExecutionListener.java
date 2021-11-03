package com.itmo.microservices.shop.common.test;

import io.prometheus.client.CollectorRegistry;
import org.jetbrains.annotations.NotNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

class ResetMetricsTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(@NotNull TestContext testContext) {
        CollectorRegistry.defaultRegistry.clear();
    }
}