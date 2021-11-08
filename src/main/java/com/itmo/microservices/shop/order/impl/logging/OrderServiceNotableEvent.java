package com.itmo.microservices.shop.order.impl.logging;

import com.itmo.microservices.commonlib.logging.NotableEvent;
import org.jetbrains.annotations.NotNull;

public enum OrderServiceNotableEvent implements NotableEvent {
    // info events
    I_ORDER_CREATED("Create order {}"),
    I_ORDER_BOOKED("Booked order {}"),
    I_ORDER_SET_TIME("Order {} delivery time set"),
    I_ITEM_ADDED("Item with UUID {} added to order"),

    // error events
    E_NO_SUCH_ORDER("No such order with UUID {}"),
    E_NO_SUCH_STATUS("No such status with name {}"),
    E_ORDER_ALREADY_BOOKED("Order with UUID {} was already booked"), // future feature
    E_CAN_NOT_CONNECT_TO_ITEM_SERVICE("Item service send error {}"), // future feature
    E_ACCESS_TO_FOREIGN_ORDER("User UUID {} is not equal to user UUID in order"); // future feature

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
