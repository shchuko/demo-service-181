package com.itmo.microservices.shop.delivery.impl.logging;

import com.itmo.microservices.commonlib.logging.NotableEvent;
import org.jetbrains.annotations.NotNull;

public enum DeliveryServiceNotableEvents implements NotableEvent {
    DELIVERY_SLOTS_REQUESTED("Requested delivery slots"),
    DELIVERY_SLOTS_REQUESTED_LIMITED("Requested delivery slots limited by {}");

    private final String template;

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
