package com.itmo.microservices.shop.delivery.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.shop.delivery.api.service.DeliveryService;
import com.itmo.microservices.shop.delivery.impl.config.ExternalDeliveryServiceCredentials;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;


class DefaultDeliveryServiceTest {
    private final DeliveryService deliveryService = new DefaultDeliveryService(new ExternalDeliveryServiceCredentials(), new EventBus());


    @ParameterizedTest
    @ValueSource(ints = {1, 3, 4, 5})
    void canGetFixedNumberOfTimeSlots(int timeSlotsLimit) {
        var timeSlots = deliveryService.getDeliverySlots(timeSlotsLimit);
        assertThat(timeSlots.size(), lessThanOrEqualTo(timeSlotsLimit));
    }
}