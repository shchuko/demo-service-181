package com.itmo.microservices.shop.order.api.exeptions;

import java.sql.Timestamp;

public class OrderAlreadyBookedException extends RuntimeException {
    String message = null;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public OrderAlreadyBookedException(String message) {
        this.message = message;
    }
}
