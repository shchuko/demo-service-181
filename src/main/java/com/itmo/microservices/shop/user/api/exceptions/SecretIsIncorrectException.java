package com.itmo.microservices.shop.user.api.exceptions;

import java.sql.Timestamp;

public class SecretIsIncorrectException extends RuntimeException {

    String message = null;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public SecretIsIncorrectException(String message) {
        this.message = message;
    }
}
