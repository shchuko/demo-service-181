package com.itmo.microservices.shop.payment.api.messaging;

import java.util.UUID;

public class PaymentCancelledEvent extends PaymentStatusEvent {
    public PaymentCancelledEvent(UUID orderId, UUID userId, String operationType) {
        super(orderId, userId, operationType);
    }
}
