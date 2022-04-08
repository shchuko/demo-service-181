package com.itmo.microservices.shop.payment.impl.metrics;

import com.itmo.microservices.shop.common.metrics.MetricEvent;
import com.itmo.microservices.shop.common.metrics.MetricType;

import java.util.Collections;
import java.util.List;

public enum PaymentMetricEvent implements MetricEvent {

    EXTERNAL_SYSTEM_EXPENSE(
            "external_system_expense",
            "The amount of money that was spent when accessing the external system",
            MetricType.COUNTER,
            Collections.emptyList(),
            "externalSystemType"
    ),

    EXTERNAL_PAYMENT_REQUEST(
            "external_payment_request",
            "The number of requests to the external system for payment. Number of successful/unsuccessful call",
            MetricType.SUMMARY,
            List.of(0.75, 0.9),
            "httpCode", "isTimeout", "accountType", "accountId"
    ),

    REVENUE(
            "revenue",
            "The amount of money that was earned with successful payments",
            MetricType.COUNTER,
            Collections.emptyList()
    ),

    REFUNDED_MONEY_AMOUNT(
            "refunded_money_amount",
            "The amount of money returned to the user",
            MetricType.COUNTER,
            Collections.emptyList(),
            "refundReason"
    ),

    PAYMENT_EXTERNAL_EXECUTOR_ACTIVE_TASKS(
            "payment_external_executor_active_tasks",
            "Number of active tasks, queue size",
            MetricType.GAUGE,
            Collections.emptyList(),
            "executorName"
    );

    public enum FAILED_TYPE {
        DELIVERY_FAILED("DELIVERY_FAILED");

        private final String title;

        FAILED_TYPE(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

    }

    private final String name;
    private final String description;
    private final String[] tagsNames;
    private final MetricType metricType;
    private final List<Double> quantiles;

    PaymentMetricEvent(String name, String description, MetricType metricType, List<Double> quantiles, String... tagsNames) {
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
