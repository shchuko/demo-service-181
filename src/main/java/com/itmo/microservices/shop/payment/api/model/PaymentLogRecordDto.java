package com.itmo.microservices.shop.payment.api.model;


import java.util.UUID;

public class PaymentLogRecordDto {
    private final Long timestamp;
    private final String paymentStatus;
    private final Integer amount;
    private final UUID transactionId;

    public PaymentLogRecordDto(Long timestamp, String paymentStatus, Integer amount, UUID transactionId) {
        this.timestamp = timestamp;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.transactionId = transactionId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Integer getAmount() {
        return amount;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

}
