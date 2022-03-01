package com.itmo.microservices.shop.common.metrics;


import java.util.List;

public interface MetricEvent {
    public abstract String getName();
    public abstract String getDescription();
    public abstract MetricType getMetricType();
    public abstract String[] getTags();
    public abstract List<Double> getQuantiles();
}
