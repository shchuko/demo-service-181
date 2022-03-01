package com.itmo.microservices.shop.order.impl.metrics;

import com.itmo.microservices.shop.common.metrics.MetricEvent;
import com.itmo.microservices.shop.common.metrics.MetricType;


public enum OrderMetricEvent implements MetricEvent {
    // counters

    ORDER_CREATED(
            "order_created",
            "Count of requests to create new order",
            MetricType.COUNTER
    ),
    ITEM_ADDED("item_added", "Count of items added to order", MetricType.COUNTER),
    ORDER_STATUS_CHANGED(
            "order_status_changed",
            "Total count of all order`s status changes",
            MetricType.COUNTER,
            "fromState", "toState"
    ),

    // gauges
    ORDER_IN_STATUS(
            "orders_in_status",
            "Current order count with certain class",
            MetricType.GAUGE,
            "status"
    );


    private final String name;
    private final String description;
    private final String[] tagsNames;
    private final MetricType metricType;

    OrderMetricEvent(String name, String description, MetricType metricType, String... tagsNames) {
        this.name = name;
        this.description = description;
        this.tagsNames = tagsNames;
        this.metricType = metricType;
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
}
