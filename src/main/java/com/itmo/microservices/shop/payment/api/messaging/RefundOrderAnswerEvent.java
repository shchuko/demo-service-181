package com.itmo.microservices.shop.payment.api.messaging;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RefundOrderAnswerEvent {
    private UUID orderUUID;
    private String refundStatus;

    @NotNull
    public UUID getOrderUUID() {
        return orderUUID;
    }

    public void setOrderUUID(@NotNull UUID orderUUID) {
        this.orderUUID = orderUUID;
    }

    @NotNull
    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(@NotNull String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public RefundOrderAnswerEvent() {
    }

    public RefundOrderAnswerEvent(@NotNull UUID orderUUID, @NotNull String refundStatus) {
        this.orderUUID = orderUUID;
        this.refundStatus = refundStatus;
    }

}
