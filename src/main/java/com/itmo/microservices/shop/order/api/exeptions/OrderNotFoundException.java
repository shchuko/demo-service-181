package com.itmo.microservices.shop.order.api.exeptions;

import java.util.UUID;

public class OrderNotFoundException extends OrderServiceException {
    public OrderNotFoundException() {
    }

    public OrderNotFoundException(String message, UUID orderId) {
        super(message, orderId);
    }

    public OrderNotFoundException(String message, Throwable cause, UUID orderId) {
        super(message, cause, orderId);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotFoundException(Throwable cause) {
        super(cause);
    }
}
