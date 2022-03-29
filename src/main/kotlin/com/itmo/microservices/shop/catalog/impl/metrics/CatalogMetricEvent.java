package com.itmo.microservices.shop.catalog.impl.metrics;

import com.itmo.microservices.shop.common.metrics.MetricEvent;
import com.itmo.microservices.shop.common.metrics.MetricType;

import java.util.Collections;
import java.util.List;


public enum CatalogMetricEvent implements MetricEvent {
    // counters
    CATALOG_SHOWN(
            "catalog_shown",
            "Count of requests to show account",
            MetricType.COUNTER,
            Collections.emptyList()
    ),
    ITEM_BOOK_REQUESTED(
            "item_booked",
            "Count of requests to book an item",
            MetricType.COUNTER,
            Collections.emptyList(),
            "status"
    );

    private final String name;
    private final String description;
    private final String[] tagsNames;
    private final MetricType metricType;
    private final List<Double> quantiles;

    CatalogMetricEvent(String name, String description, MetricType metricType, List<Double> quantiles, String... tagsNames) {
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
