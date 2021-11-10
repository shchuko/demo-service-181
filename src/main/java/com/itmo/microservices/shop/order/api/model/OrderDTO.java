package com.itmo.microservices.shop.order.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class OrderDTO {
    @JsonProperty("id")
    private UUID uuid;

    @JsonProperty("timeCreated")
    private long timeCreated;

    @JsonProperty("status")
    private String status;  

    @JsonProperty("itemsMap")
    private Map<OrderItemDTO,Integer> itemsMap;

    @JsonProperty("deliveryDuration")
    private long deliveryDuration;

    @JsonProperty("paymentHistory")
    private List<PaymentLogRecord> paymentHistory;
}
