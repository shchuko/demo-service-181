package com.itmo.microservices.shop.order.api.messaging;

import java.util.UUID;

public class OrderSuccessPaidEvent {
    private UUID orderID;
    private UUID userID;
    private Integer amount;

    public OrderSuccessPaidEvent(UUID orderID, UUID userID, Integer amount) {
        this.orderID = orderID;
        this.userID = userID;
        this.amount = amount;
    }

    public UUID getOrderID() {
        return orderID;
    }

    public void setOrderID(UUID orderID) {
        this.orderID = orderID;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
