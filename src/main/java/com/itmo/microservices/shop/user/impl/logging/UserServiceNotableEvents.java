package com.itmo.microservices.shop.user.impl.logging;

import com.itmo.microservices.commonlib.logging.NotableEvent;
import org.jetbrains.annotations.NotNull;

public enum UserServiceNotableEvents implements NotableEvent {
    I_USER_CREATED("User created: {}"),
    E_USER_ALREADY_CREATED("User already created: {}"),
    I_USER_FOUND_BY_UUID("User found by uuid: {}"),
    E_USER_NOT_FOUND_BY_UUID("User not found by uuid: {}"),
    I_USER_FOUND_BY_NAME("User found by username: {}"),
    E_USER_NOT_FOUND_BY_USERNAME("User not found by username: {}");

    private final String template;

    UserServiceNotableEvents(String template) {
        this.template = template;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name();
    }

    @NotNull
    @Override
    public String getTemplate() {
        return this.template;
    }
}
