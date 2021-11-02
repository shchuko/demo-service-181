package com.itmo.microservices.shop.catalog.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.Timestamp;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {

    String message = null;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public ItemNotFoundException(String message) {
        this.message = message;
    }

}
