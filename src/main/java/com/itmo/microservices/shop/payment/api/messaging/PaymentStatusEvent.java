package com.itmo.microservices.shop.payment.api.messaging;

import java.util.UUID;

public abstract class PaymentStatusEvent {
    private UUID orderId;
    private UUID userId;
    private String operationType;

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public PaymentStatusEvent(UUID orderId, UUID userId, String operationType) {
        this.orderId = orderId;
        this.userId = userId;
        this.operationType = operationType;
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
