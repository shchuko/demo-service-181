package com.itmo.microservices.shop.user.api.exceptions;

import java.sql.Timestamp;

public class UserNotFoundException extends RuntimeException {

    String message = null;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public UserNotFoundException(String message) {
        this.message = message;
    }
}