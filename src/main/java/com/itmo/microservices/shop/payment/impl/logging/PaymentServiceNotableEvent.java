package com.itmo.microservices.shop.payment.impl.logging;

import com.itmo.microservices.commonlib.logging.NotableEvent;
import org.jetbrains.annotations.NotNull;

public enum PaymentServiceNotableEvent implements NotableEvent {
    // Errors
    E_REFUND_ALREADY_REQUESTED("Refund for order: {} already requested"),
    E_REFUND_EXCEPTION_SCHEDULER("Exception for order: {}: in the scheduler"),
    E_REFUND_TIMEOUT("The request to an external service timed out for order: {}"),
    E_REFUND_ALREADY_DONE("Refunds for order: {} have already been processed successfully."),

    // Info
    I_REFUND_REQUEST_ADDED_SUCCESSFULLY("Refund request for order: {} has been successfully added"),
    I_REFUND_REQUEST_WAS_SUCCESSFUL("The refund for the order: {} was successful."),
    I_PAYMENT_REQUEST_ADDED_SUCCESSFULLY("Payment request for order: {} has been successfully added")
    ;

    private final String template;

    PaymentServiceNotableEvent(String orderId) {
        this.template = orderId;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name();
    }

    @NotNull
    @Override
    public String getTemplate() {
        return template;
    }
}
