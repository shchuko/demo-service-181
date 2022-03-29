package com.itmo.microservices.shop.common.metrics;

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MetricCollector extends CommonMetricsCollector {
    private final static String SERVICE_NAME_TAG = "serviceName";
    private final static Double QUANTILE_ERROR = 0.001;
    private final String applicationName;
    private static final Map<String, SimpleCollector<?>> meters = new HashMap<>();
    private final PrometheusMeterRegistry registry;

    public MetricCollector(
            PrometheusMeterRegistry registry,
            @Value("${spring.application.name}") String applicationName
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


    public void passEvent(MetricEvent event, double value, String... tagsRaw) {
        this.validateOrThrow(value, event.getMetricType());
        var tags = appendServiceNameTag(tagsRaw);
        switch (event.getMetricType()) {
            case GAUGE:
                Gauge gauge = (Gauge) this.getCollector(event);
                if (value < 0) {
                    gauge.labels(tags).inc(value);
                } else {
                    gauge.labels(tags).dec(-value);
                }
                break;
            case COUNTER:
                Counter counter = (Counter) this.getCollector(event);
                counter.labels(tags).inc(value);
                break;
            case SUMMARY:
                Summary summary = (Summary) this.getCollector(event);
                summary.labels(tags).observe(value);
                break;
            default:
                throw new IllegalStateException("No logic for this metric type: " + event.getMetricType());
        }
    }

    private void validateOrThrow(double value, MetricType metricType) {
        switch (metricType) {
            case COUNTER:
            case SUMMARY:
                if (value < 0) {
                    throw new IllegalArgumentException("Value must be positive");
                }
                break;
            case GAUGE:
                break;
            default:
                throw new IllegalStateException("No logic for this metric type: " + metricType);
        }
    }

    private Counter generateCounter(MetricEvent event) {
        return Counter
                .build()
                .name(event.getName())
                .help(event.getDescription())
                .labelNames(appendServiceNameLabel(event.getTags()))
                .register(this.registry.getPrometheusRegistry());
    }

    private Gauge generateGauge(MetricEvent event) {
        return Gauge
                .build()
                .name(event.getName())
                .help(event.getDescription())
                .labelNames(appendServiceNameLabel(event.getTags()))
                .register(this.registry.getPrometheusRegistry());
    }

    private Summary generateSummary(MetricEvent event) {
        Summary.Builder summaryBuilder = Summary
                .build()
                .name(event.getName())
                .help(event.getDescription())
                .labelNames(appendServiceNameLabel(event.getTags()));
        for (Double quantile: event.getQuantiles()) {
            summaryBuilder = summaryBuilder.quantile(quantile, QUANTILE_ERROR);
        }
        return summaryBuilder.register(this.registry.getPrometheusRegistry());
    }

    private SimpleCollector<?> getCollector(MetricEvent event) {
        return meters.get(event.getName());
    }

    public void register(MetricEvent... events) {
        for (var event : events) {
            SimpleCollector<?> collector;
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
                    throw new IllegalStateException("No logic for this metric type: " + event.getMetricType());
            }
            meters.put(event.getName(), collector);
        }
    }

    /* TODO remove this kostyl */
    private String[] appendServiceNameTag(String[] tags) {
        var list = new ArrayList<>(Arrays.asList(tags));
        list.add(applicationName);
        return list.toArray(new String[0]);
    }

    /* TODO remove this kostyl */
    private String[] appendServiceNameLabel(String[] tags) {
        var list = new ArrayList<>(Arrays.asList(tags));
        list.add(SERVICE_NAME_TAG);
        return list.toArray(new String[0]);
    }
}
