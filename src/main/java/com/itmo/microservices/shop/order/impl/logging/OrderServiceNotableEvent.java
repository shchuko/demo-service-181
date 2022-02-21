package com.itmo.microservices.shop.order.impl.logging;

import com.itmo.microservices.commonlib.logging.NotableEvent;
import org.jetbrains.annotations.NotNull;

public enum OrderServiceNotableEvent implements NotableEvent {
    // info events
    I_ORDER_CREATED("Create order {}"),
    I_ORDER_FINALIZED("Booked order {}"),
    I_ORDER_UNBOOKED("Booking cancelled for order {}"),
    I_ORDER_START_DELIVERY("Order {} delivery start"),
    I_ORDER_SUCCESSFUL_DELIVERY("Order {} delivery success"),
    I_ORDER_FAILED_DELIVERY("Order {} delivery failed"),
    I_ORDER_SUBMIT_PAYMENT("Order {} payment submitted"),
    I_ORDER_SUCCESSFUL_PAYMENT("Order {} payment success"),
    I_ORDER_FAILED_PAYMENT("Order {} payment failed"),
    I_ORDER_CANCELLED_PAYMENT("Order {} payment cancelled"),
    I_ORDER_SET_TIME_SLOT("Order {} delivery time slot set"),
    I_ITEM_ADDED("Item with UUID {} added to order"),
    I_REFUND_STARTED("Refund started for order {}"),
    I_REFUND_DONE("Refund done for order {}"),

    // error events
    E_NO_SUCH_ORDER("No such order with UUID {}"),
    E_ORDER_SUBMIT_PAYMENT("Order payment submit error: already exists for order {}"),
    E_CONFLICT("Conflict while order processing {}"),
    E_ILLEGAL_STATE("Illegal state {}"),
    E_ITEM_NOT_FOUND("Item not found {}"),
    E_NOT_ALLOWED("Attempt to access order {} rejected");

    private final String template;

    OrderServiceNotableEvent(String order) {
        this.template = order;
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
