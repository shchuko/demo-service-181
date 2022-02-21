package com.itmo.microservices.shop.order.api.exeptions;

import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;

import java.util.UUID;

public class NotEnoughPermissions extends OrderServiceException {
    public NotEnoughPermissions() {
    }

    public NotEnoughPermissions(String message, UUID orderId) {
        super(message, orderId);
    }

    public NotEnoughPermissions(String message, Throwable cause, UUID orderId) {
        super(message, cause, orderId);
    }

    public NotEnoughPermissions(String message, UUID orderId, IOrderStatusRepository.StatusNames status) {
        super(message, orderId, status);
    }

    public NotEnoughPermissions(String message, Throwable cause, UUID orderId, IOrderStatusRepository.StatusNames status) {
        super(message, cause, orderId, status);
    }

    public NotEnoughPermissions(String message) {
        super(message);
    }

    public NotEnoughPermissions(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughPermissions(Throwable cause) {
        super(cause);
    }
}
