package com.itmo.microservices.shop.payment.impl.exceptions;

public class PaymentAlreadyExistsException extends PaymentException {
    public PaymentAlreadyExistsException() {
    }

    public PaymentAlreadyExistsException(String message) {
        super(message);
    }

    public PaymentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
