package com.itmo.microservices.shop.order.api.messaging;

import java.util.UUID;

public class OrderStartDeliveryTransactionEvent {
    private UUID orderID;
    private UUID userID;
    private int timeSlot;

    public OrderStartDeliveryTransactionEvent(UUID orderID, UUID userID, int timeSlot) {
        this.orderID = orderID;
        this.userID = userID;
        this.timeSlot = timeSlot;
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

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }
}
