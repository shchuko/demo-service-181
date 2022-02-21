package com.itmo.microservices.shop.order.api.exeptions;

import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;

import java.util.UUID;

public class BadOperationForCurrentOrderStatus extends OrderServiceConflictException {
    public BadOperationForCurrentOrderStatus() {
    }

    public BadOperationForCurrentOrderStatus(String message, UUID orderId) {
        super(message, orderId);
    }

    public BadOperationForCurrentOrderStatus(String message, Throwable cause, UUID orderId) {
        super(message, cause, orderId);
    }

    public BadOperationForCurrentOrderStatus(String message, UUID orderId, IOrderStatusRepository.StatusNames status) {
        super(message, orderId, status);
    }

    public BadOperationForCurrentOrderStatus(String message, Throwable cause, UUID orderId, IOrderStatusRepository.StatusNames status) {
        super(message, cause, orderId, status);
    }

    public BadOperationForCurrentOrderStatus(String message) {
        super(message);
    }

    public BadOperationForCurrentOrderStatus(String message, Throwable cause) {
        super(message, cause);
    }

    public BadOperationForCurrentOrderStatus(Throwable cause) {
        super(cause);
    }
}
