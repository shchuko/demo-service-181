package com.itmo.microservices.shop.delivery.api.messaging;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DeliveryStatusFailedEvent extends DeliveryStatusEvent {
    public DeliveryStatusFailedEvent(@NotNull UUID orderId, @NotNull UUID userId, int timeSlot) {
        super(orderId, userId, timeSlot);
    }
}
