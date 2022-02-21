package com.itmo.microservices.shop.payment.api.messaging;

import com.itmo.microservices.shop.payment.impl.repository.FinancialOperationTypeRepository;

import java.util.UUID;

public class PaymentFailedEvent extends PaymentStatusEvent {
    public PaymentFailedEvent(UUID orderId, UUID userId, String operationType) {
        super(orderId, userId, operationType);
    }
}
