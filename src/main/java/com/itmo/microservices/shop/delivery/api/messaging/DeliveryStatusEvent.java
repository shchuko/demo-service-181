package com.itmo.microservices.shop.delivery.api.messaging;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class DeliveryStatusEvent {
    private UUID orderId;
    private UUID userId;
    private int timeSlot;

    public DeliveryStatusEvent(@NotNull UUID orderId, @NotNull UUID userId, int timeSlot) {
        this.orderId = orderId;
        this.userId = userId;
        this.timeSlot = timeSlot;
    }

    @NotNull
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(@NotNull UUID orderId) {
        this.orderId = orderId;
    }

    @NotNull
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(@NotNull UUID userId) {
        this.userId = userId;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

}
