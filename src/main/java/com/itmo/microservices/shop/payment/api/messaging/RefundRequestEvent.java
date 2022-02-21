package com.itmo.microservices.shop.payment.api.messaging;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RefundRequestEvent {
    private UUID orderId;
    private UUID userId;
    private int amount;

    public RefundRequestEvent() {
    }

    public RefundRequestEvent(@NotNull UUID orderId, @NotNull UUID userId, int amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
