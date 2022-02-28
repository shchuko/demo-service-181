package com.itmo.microservices.shop.common.metrics

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector
import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import org.springframework.stereotype.Component

@Component
class DemoServiceMetricsCollector(serviceName: String): CommonMetricsCollector(serviceName) {
    constructor() : this(SERVICE_NAME)

    companion object {
        const val SERVICE_NAME = "p81"
    }

    fun generateGauge(name: String, help: String): Gauge {
        return Gauge.build()
                .name(name)
                .help(help)
                .register()
    }

    fun generateCounter(name: String, help: String): Counter {
        return Counter.build()
                .name(name)
                .help(help)
                .register()
    }
}
