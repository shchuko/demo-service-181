package com.itmo.microservices.shop.delivery.impl.logging;

import com.itmo.microservices.commonlib.logging.NotableEvent;
import org.jetbrains.annotations.NotNull;

public enum DeliveryServiceNotableEvents implements NotableEvent {
    ORDER_ID_IS_NOT_FOUND("Order id is :{}"),
    ORDER_ASSIGNED_TO_TIMESLOT("Order is assigned to timeslot : {}");

    private String template;

    DeliveryServiceNotableEvents(String template) {
        this.template = template;
    }

    @NotNull
    @Override
    public String getName() {
        return template;
    }

    @NotNull
    @Override
    public String getTemplate() {
        return template;
    }
}
