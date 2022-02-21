package com.itmo.microservices.shop.payment.api.messaging;

import com.itmo.microservices.shop.payment.impl.repository.FinancialOperationTypeRepository;

import java.util.UUID;

public class PaymentFailedEvent extends PaymentStatusEvent {
    private final FinancialOperationTypeRepository.VALUES operationType;

    public PaymentFailedEvent(UUID orderId, UUID userId, FinancialOperationTypeRepository.VALUES operationType) {
        super(orderId, userId);
        this.operationType = operationType;
    }

    public FinancialOperationTypeRepository.VALUES getOperationType() {
        return operationType;
    }
}
