package com.itmo.microservices.shop.delivery.api.messaging;

public class DeliveryTransactionPassEvent {
    public DeliveryTransactionPassEvent() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;
}
