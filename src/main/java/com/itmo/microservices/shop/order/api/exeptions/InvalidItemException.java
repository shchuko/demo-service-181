package com.itmo.microservices.shop.order.api.exeptions;

import java.sql.Timestamp;

public class InvalidItemException extends RuntimeException {
    String message = null;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public InvalidItemException(String message) {
        this.message = message;
    }
}
