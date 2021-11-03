package com.itmo.microservices.shop.delivery.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode
public class DeliveryInfoModel {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("orderId")
    private UUID orderId;

    @JsonProperty("isDelivered")
    private Boolean isDelivered;

    @JsonProperty("startDeliveryAt")
    private long startDeliveryAt;

    @JsonProperty("address")
    private String address;

    @JsonProperty("slot")
    private int slot;


    public DeliveryInfoModel() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Boolean getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(Boolean delivered) {
        isDelivered = delivered;
    }

    public long getStartDeliveryAt() {
        return startDeliveryAt;
    }

    public void setStartDeliveryAt(long startDeliveryAt) {
        this.startDeliveryAt = startDeliveryAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
