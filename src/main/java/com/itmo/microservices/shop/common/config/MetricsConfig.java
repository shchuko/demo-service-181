package com.itmo.microservices.shop.common.config;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

import java.util.LinkedList;
import java.util.List;


/*

@Configuration
public class MetricsConfig {

    @Value("${spring.application.name}")
    String applicationName;

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        System.out.println("IN CUSTOMIZER_TIMER");
        Metrics.addRegistry(registry);
        return new TimedAspect(registry);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        List<Tag> tags = new LinkedList<>();
        tags.add(Tag.of("serviceName1", applicationName));
        System.out.println("IN CUSTOMIZER");
        //registry -> registry.config().commonTags(tags)
        //Metrics.addRegistry(registry);

        return registry -> registry.config().commonTags(tags);
    }
}
*/

