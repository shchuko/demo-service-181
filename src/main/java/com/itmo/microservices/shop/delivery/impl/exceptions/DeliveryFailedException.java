package com.itmo.microservices.shop.delivery.impl.exceptions;

public class DeliveryFailedException extends Exception {
    public DeliveryFailedException(String message) {
        super(message);
    }
}
