package com.itmo.microservices.shop.delivery.api.messaging;

import java.util.UUID;

public class DeliveryTransactionFailedEvent extends DeliveryTransactionEvent {
    public DeliveryTransactionFailedEvent(UUID orderId, UUID userId, int timeSlot) {
        super(orderId, userId, timeSlot);
    }
}
