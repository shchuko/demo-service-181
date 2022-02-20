package com.itmo.microservices.shop.payment.impl.exceptions;

public class PaymentInUninterruptibleProcessing extends PaymentException {
    public PaymentInUninterruptibleProcessing() {
    }

    public PaymentInUninterruptibleProcessing(String message) {
        super(message);
    }

    public PaymentInUninterruptibleProcessing(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentInUninterruptibleProcessing(Throwable cause) {
        super(cause);
    }
}
