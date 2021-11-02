package com.itmo.microservices.shop.order.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmo.microservices.shop.payment.impl.entity.PaymentStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class PaymentLogRecord {
    @JsonProperty("transactionId")
    private UUID uuid;

    @JsonProperty("timestamp")
    private long timeStamp;

    @JsonProperty("status")
    private PaymentStatus status;

    @JsonProperty("amount")
    private int amount;
}
