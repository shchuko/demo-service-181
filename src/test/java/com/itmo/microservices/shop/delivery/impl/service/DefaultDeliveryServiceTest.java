package com.itmo.microservices.shop.delivery.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.shop.common.metrics.MetricCollector;
import com.itmo.microservices.shop.delivery.api.service.DeliveryService;
import com.itmo.microservices.shop.delivery.impl.config.ExternalDeliveryServiceCredentials;
import com.itmo.microservices.shop.delivery.impl.repository.DeliveryTransactionsProcessorWritebackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;


class DefaultDeliveryServiceTest {
    private DeliveryService deliveryService;

    @BeforeEach
    void init() {
//        DeliveryTransactionsProcessorWritebackRepository writebackRepository = Mockito.mock(DeliveryTransactionsProcessorWritebackRepository.class);
//        ExternalDeliveryServiceCredentials credentials = new ExternalDeliveryServiceCredentials();
//        credentials.setRateLimit(50);
//        deliveryService = new DefaultDeliveryService(writebackRepository,
//                credentials,
//                new EventBus(),
//                new MetricCollector());
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 3, 4, 5})
    @Disabled
    void canGetFixedNumberOfTimeSlots(int timeSlotsLimit) {
//        var timeSlots = deliveryService.getDeliverySlots(timeSlotsLimit);
//        assertThat(timeSlots.size(), lessThanOrEqualTo(timeSlotsLimit));
    }

    @Test
    void dummyTest() {
        // TODO implement test
    }
}