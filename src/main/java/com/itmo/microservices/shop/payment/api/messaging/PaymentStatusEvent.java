package com.itmo.microservices.shop.payment.api.messaging;

import java.util.UUID;

public abstract class PaymentStatusEvent {
    private UUID orderId;
    private UUID userId;

    public PaymentStatusEvent(UUID orderId, UUID userId) {
        this.orderId = orderId;
        this.userId = userId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
