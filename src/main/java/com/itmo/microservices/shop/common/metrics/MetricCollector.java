package com.itmo.microservices.shop.common.metrics;

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.*;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MetricCollector extends CommonMetricsCollector {
    private String applicationName;

    // TODO: catch `IllegalArgumentException` || add concurrency logic,
    //      on first call, if there are several parallel requests, it will try to create already exist `Meter`
    //      due to non exist key at this time
    private static Map<String, SimpleCollector> meters = new HashMap<>();
    private final PrometheusMeterRegistry registry;

    public MetricCollector(
            PrometheusMeterRegistry registry,
            @Value("${spring.application.name}")String applicationName
    ) {
        super(applicationName);
        this.applicationName = applicationName;
        this.registry = registry;

        Metrics.addRegistry(registry); // to add `events_total`(metrics from common-lib) to actuator
    }

/*
    //affects every metric except our
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> commonTags() {
        List<Tag> tags = new LinkedList<>();
        tags.add(Tag.of("serviceName3", this.applicationName));
        registry.config().commonTags(tags);
        return (registry) -> registry.config()
                .commonTags(tags);
    }

    //affects every metric except our
    @PostConstruct
    public void configure() {
        List<Tag> tags = new LinkedList<>();
        tags.add(Tag.of("serviceName2", this.applicationName));
        registry.config().commonTags(tags);
    }
*/
    public void passEvent(MetricEvent event, double value, String... tags) {
        boolean isInc = this.validateValue(value, event.getMetricType());
        switch (event.getMetricType()){
            case GAUGE:
                Gauge gauge = (Gauge) this.getOrCreateCollector(event);
                if (isInc) {
                    gauge.labels(tags).inc(value);
                } else {
                    gauge.labels(tags).dec(value);
                }
                break;
            case COUNTER:
                Counter counter = (Counter) this.getOrCreateCollector(event);
                counter.labels(tags).inc(value);
                break;
            case SUMMARY:
                // TODO: implement summary.record()
                break;
            default:
                throw new NotImplementedException("No logic for this metric type: " + event.getMetricType());
        }
    }

    private boolean validateValue(double value, MetricType metricType) {
        switch (metricType){
            case GAUGE:
                return (value >= 0);
            case COUNTER:
            case SUMMARY:
                if (value < 0) {
                    throw new IllegalArgumentException("Value must be positive");
                }
                return true;
            default:
                throw new NotImplementedException("No logic for this metric type: " + metricType);
        }
    }

    private Counter generateCounter(MetricEvent event) {
        return Counter
                .build()
                .name(event.getName())
                .help(event.getDescription())
                .labelNames(event.getTags())
                .register(this.registry.getPrometheusRegistry());
    }

    private Gauge generateGauge(MetricEvent event) {
        return Gauge
                .build()
                .name(event.getName())
                .help(event.getDescription())
                .labelNames(event.getTags())
                .register(this.registry.getPrometheusRegistry());
    }

    // TODO: add quantiles
    private Summary generateSummary(MetricEvent event) {
        return Summary
                .build()
                .name(event.getName())
                .help(event.getDescription())
                .labelNames(event.getTags())
                .register(this.registry.getPrometheusRegistry());
    }

    private SimpleCollector getOrCreateCollector(MetricEvent event) {
        SimpleCollector collector = meters.getOrDefault(event.getName(), null);
        if (collector == null) {
            switch (event.getMetricType()) {
                case SUMMARY:
                    collector = this.generateSummary(event);
                    break;
                case COUNTER:
                    collector = this.generateCounter(event);
                    break;
                case GAUGE:
                    collector = this.generateGauge(event);
                    break;
                default:
                    throw new NotImplementedException("No logic for this metric type: " + event.getMetricType());
            }
            meters.put(event.getName(), collector);
        }
        return collector;
    }
}
