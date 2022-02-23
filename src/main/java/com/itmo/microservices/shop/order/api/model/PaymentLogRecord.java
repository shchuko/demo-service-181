package com.itmo.microservices.shop.order.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmo.microservices.shop.payment.api.model.PaymentLogRecordDto;
import com.itmo.microservices.shop.payment.impl.entity.PaymentStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class PaymentLogRecord {
    @JsonProperty("transactionId")
    private UUID transactionId;

    @JsonProperty("timestamp")
    private long timeStamp;

    @JsonProperty("status")
    private String status;

    @JsonProperty("amount")
    private int amount;

    public PaymentLogRecord(PaymentLogRecordDto paymentLogRecordDto) {
        this.transactionId = paymentLogRecordDto.getTransactionId();
        this.timeStamp = paymentLogRecordDto.getTimestamp();
        this.status = paymentLogRecordDto.getPaymentStatus();
        this.amount = paymentLogRecordDto.getAmount();
    }
}
