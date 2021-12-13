package com.itmo.microservices.shop.delivery.api.messaging;

import java.util.UUID;

public abstract class DeliveryTransactionEvent {
    private UUID orderId;
    private UUID userId;
    private int timeSlot;

    public DeliveryTransactionEvent(UUID orderId, UUID userId, int timeSlot) {
        this.orderId = orderId;
        this.userId = userId;
        this.timeSlot = timeSlot;
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

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

}
