package com.itmo.microservices.shop.delivery.impl.metrics;

import com.itmo.microservices.shop.common.metrics.MetricEvent;
import com.itmo.microservices.shop.common.metrics.MetricType;

import java.util.Collections;
import java.util.List;

public enum DeliveryMetricEvent implements MetricEvent {
    // counters
    SHIPPING_ORDERS_TOTAL(
            "shipping_orders_total",
            "Number of orders sent for delivery",
            MetricType.COUNTER,
            Collections.emptyList()
    ),

    // gauges
    CURRENT_SHIPPING_ORDERS(
            "current_shipping_orders",
            "Number of orders currently in delivery",
            MetricType.GAUGE,
            Collections.emptyList()
    ),
    DELIVERY_EXTERNAL_EXECUTOR_ACTIVE_TASKS(
            "delivery_external_executor_active_tasks",
            "number of active tasks, queue size",
            MetricType.GAUGE,
            Collections.emptyList(),
            "executorName"
    ),

    // summary
    DELIVERY_EXTERNAL_REQUESTS(
            "delivery_external_requests",
            "The number of requests to the external system for delivery. Number of successful/unsuccessful calls.",
            MetricType.SUMMARY,
            List.of(0.75, 0.9),
            "httpCode", "isTimeout", "accountType"
    );

    private final String name;
    private final String description;
    private final String[] tagsNames;
    private final MetricType metricType;
    private final List<Double> quantiles;

    DeliveryMetricEvent(String name, String description, MetricType metricType, List<Double> quantiles, String... tagsNames) {
        this.name = name;
        this.description = description;
        this.tagsNames = tagsNames;
        this.metricType = metricType;
        this.quantiles = quantiles;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public MetricType getMetricType() {
        return this.metricType;
    }

    @Override
    public String[] getTags() {
        return this.tagsNames;
    }

    @Override
    public List<Double> getQuantiles() {
        return quantiles;
    }
}
