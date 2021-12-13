package com.itmo.microservices.shop.delivery.api.messaging;

import java.util.UUID;

public class DeliveryTransactionSuccessEvent extends DeliveryTransactionEvent {
    public DeliveryTransactionSuccessEvent(UUID orderId, UUID userId, int timeSlot) {
        super(orderId, userId, timeSlot);
    }
}
