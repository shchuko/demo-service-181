package com.itmo.microservices.shop.common.metrics;


public interface MetricEvent {
    public abstract String getName();
    public abstract String getDescription();
    public abstract MetricType getMetricType();
    public abstract String[] getTags();

}
