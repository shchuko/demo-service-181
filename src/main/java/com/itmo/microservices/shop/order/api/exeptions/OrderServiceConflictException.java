package com.itmo.microservices.shop.order.api.exeptions;

import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;

import java.util.UUID;

public abstract class OrderServiceConflictException extends OrderServiceException {
    public OrderServiceConflictException() {
    }

    public OrderServiceConflictException(String message, UUID orderId) {
        super(message, orderId);
    }

    public OrderServiceConflictException(String message, Throwable cause, UUID orderId) {
        super(message, cause, orderId);
    }

    public OrderServiceConflictException(String message, UUID orderId, IOrderStatusRepository.StatusNames status) {
        super(message, orderId, status);
    }

    public OrderServiceConflictException(String message, Throwable cause, UUID orderId, IOrderStatusRepository.StatusNames status) {
        super(message, cause, orderId, status);
    }

    public OrderServiceConflictException(String message) {
        super(message);
    }

    public OrderServiceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderServiceConflictException(Throwable cause) {
        super(cause);
    }
}
