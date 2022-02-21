package com.itmo.microservices.shop.order.api.exeptions;

import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;

import java.util.UUID;

public abstract class OrderServiceException extends RuntimeException {
    private UUID orderId;
    private IOrderStatusRepository.StatusNames status;

    public OrderServiceException() {
    }

    public OrderServiceException(String message, UUID orderId) {
        super(message + ": orderId='" + orderId + "'");
    }

    public OrderServiceException(String message, Throwable cause, UUID orderId) {
        super(message + ": orderId='" + orderId + "'", cause);
    }

    public OrderServiceException(String message, UUID orderId, IOrderStatusRepository.StatusNames status) {
        super(message + ": orderId='" + orderId + "', status='" + status.name() + "'");
    }

    public OrderServiceException(String message, Throwable cause, UUID orderId, IOrderStatusRepository.StatusNames status) {
        super(message + ": orderId='" + orderId + "', status='" + status.name() + "'", cause);
    }

    public OrderServiceException(String message) {
        super(message);
    }

    public OrderServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderServiceException(Throwable cause) {
        super(cause);
    }
}
