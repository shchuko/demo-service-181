package com.itmo.microservices.shop.delivery.api.messaging;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DeliveryStatusSuccessEvent extends DeliveryStatusEvent {
    private final int deliveryDuration;

    public DeliveryStatusSuccessEvent(@NotNull UUID orderId, @NotNull UUID userId, int timeSlot, int deliveryDuration) {
        super(orderId, userId, timeSlot);
        this.deliveryDuration = deliveryDuration;
    }

    public int getDeliveryDuration() {
        return deliveryDuration;
    }
}
