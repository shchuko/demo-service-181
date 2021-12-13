package com.itmo.microservices.shop.order.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class OrderDTO {
    @JsonProperty("id")
    private UUID uuid;

    @JsonProperty("timeCreated")
    private long timeCreated;

    @JsonProperty("status")
    private String status;

    @JsonProperty("itemsMap")
    private Map<UUID, Integer> itemsMap;

    @JsonProperty("deliveryDuration")
    private long deliveryDuration;

    @JsonProperty("paymentHistory")
    private List<PaymentLogRecord> paymentHistory;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<UUID, Integer> getItemsMap() {
        return itemsMap;
    }

    public void setItemsMap(Map<UUID, Integer> itemsMap) {
        this.itemsMap = itemsMap;
    }

    public long getDeliveryDuration() {
        return deliveryDuration;
    }

    public void setDeliveryDuration(long deliveryDuration) {
        this.deliveryDuration = deliveryDuration;
    }

    public List<PaymentLogRecord> getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(List<PaymentLogRecord> paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderDTO orderDTO = (OrderDTO) o;
        return Objects.equals(uuid, orderDTO.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
