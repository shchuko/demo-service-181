package com.itmo.microservices.shop.delivery.api.service;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
}
