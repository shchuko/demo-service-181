package com.itmo.microservices.shop.payment.api.model;

import java.util.UUID;

public class UserAccountFinancialLogRecordDto {
    private final String FinancialOperationType;
    private final Integer amount;
    private final UUID orderId;
    private final UUID paymentTransactionId;
    private final Long timestamp;

    public UserAccountFinancialLogRecordDto(String financialOperationType,
                                            Integer amount, UUID orderId,
                                            UUID paymentTransactionId,
                                            Long timestamp) {
        FinancialOperationType = financialOperationType;
        this.amount = amount;
        this.orderId = orderId;
        this.paymentTransactionId = paymentTransactionId;
        this.timestamp = timestamp;
    }

    public String getFinancialOperationType() {
        return FinancialOperationType;
    }

    public Integer getAmount() {
        return amount;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
