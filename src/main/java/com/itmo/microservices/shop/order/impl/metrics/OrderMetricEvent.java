package com.itmo.microservices.shop.order.impl.metrics;

import com.itmo.microservices.shop.common.metrics.MetricEvent;
import com.itmo.microservices.shop.common.metrics.MetricType;

import java.util.Collections;
import java.util.List;


public enum OrderMetricEvent implements MetricEvent {
    // counters
    ORDER_CREATED(
            "order_created",
            "Count of requests to create new order",
            MetricType.COUNTER,
            Collections.emptyList()
    ),
    ITEM_ADDED(
            "item_added",
            "Count of items added to order",
            MetricType.COUNTER,
            Collections.emptyList()
    ),
    ORDER_STATUS_CHANGED(
            "order_status_changed",
            "Total count of all order`s status changes",
            MetricType.COUNTER,
            Collections.emptyList(),
            "fromState", "toState"
    ),
    FINALIZATION_ATTEMPT(
            "finalization_attempt",
            "Count of requests to finalize order with request status",
            MetricType.COUNTER,
            Collections.emptyList(),
            "result"
    ),
    TIMESLOT_SET_REQUEST_COUNT(
            "timeslot_set_request_count",
            "Count of requests to set time slot",
            MetricType.COUNTER,
            Collections.emptyList()
    ),
    ADD_TO_FINALIZED_ORDER_REQUEST(
            "add_to_finalized_order_request",
            "Count of requests to add items to already finalized order",
            MetricType.COUNTER,
            Collections.emptyList()
    ),
    // TODO: discarded_orders
    DISCARDED_ORDERS(
            "discarded_orders",
            "Count of discarded order",
            MetricType.COUNTER,
            Collections.emptyList()
    ),

    // gauges
    ORDER_IN_STATUS(
            "orders_in_status",
            "Current order count with certain class",
            MetricType.GAUGE,
            Collections.emptyList(),
            "status"
    ),
    // TODO: current_abandoned_order_num
    CURRENT_ABANDONED_ORDER_NUM(
            "current_abandoned_order_num",
            "Current number of abandoned order",
            MetricType.GAUGE,
            Collections.emptyList()
    ),

    // summary
    FINALIZATION_DURATION(
            "finalization_duration",
            "Duration of finalizing order",
            MetricType.SUMMARY,
            List.of(0.9)
    );


    private final String name;
    private final String description;
    private final String[] tagsNames;
    private final MetricType metricType;
    private final List<Double> quantiles;

    OrderMetricEvent(String name, String description, MetricType metricType, List<Double> quantiles, String... tagsNames) {
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
