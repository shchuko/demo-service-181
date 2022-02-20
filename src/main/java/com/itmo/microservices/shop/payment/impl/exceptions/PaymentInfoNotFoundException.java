package com.itmo.microservices.shop.payment.impl.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PaymentInfoNotFoundException extends PaymentException {
    public PaymentInfoNotFoundException(String message) {
        super(message);
    }
}
