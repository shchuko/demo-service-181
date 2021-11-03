package com.itmo.microservices.shop.common.test;

import com.itmo.microservices.shop.common.security.NoSecurityConfigurerAdapterConfig;
import com.itmo.microservices.shop.common.security.SecurityConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;

@SpringBootTest
@TestExecutionListeners(
        listeners = ResetMetricsTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class DefaultSecurityTestCase {
}
