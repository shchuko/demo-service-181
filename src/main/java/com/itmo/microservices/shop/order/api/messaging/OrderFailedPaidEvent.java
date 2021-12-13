package com.itmo.microservices.shop.order.api.messaging;

import java.util.UUID;

public class OrderFailedPaidEvent {
    private UUID orderID;
    private UUID userID;

    public OrderFailedPaidEvent(UUID orderID, UUID userID, Integer amount) {
        this.orderID = orderID;
        this.userID = userID;
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
}
