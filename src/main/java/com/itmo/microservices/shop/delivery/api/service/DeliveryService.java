package com.itmo.microservices.shop.delivery.api.service;

import com.itmo.microservices.shop.delivery.api.messaging.StartDeliveryEvent;
import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoRecordDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {
    /**
     * Get limited number of available delivery slots
     *
     * @param number Number of slots we want to get
     * @return List of delivery slots limited by number
     */
    @NotNull
    List<Integer> getDeliverySlots(int number);

    /**
     * Get available delivery slots
     *
     * @return List of available delivery slots
     */
    @NotNull
    List<Integer> getDeliverySlots();

    /**
     * Trigger delivery start for order from event.
     *
     * @param event Event describes order to deliver.
     */
    void handleStartDelivery(@NotNull StartDeliveryEvent event);

    /**
     * Get delivery log for specific order
     *
     * @param orderId Order ID
     * @return Delivery log
     */
    List<DeliveryInfoRecordDto> getDeliveryLog(UUID orderId);
}
