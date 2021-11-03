package com.itmo.microservices.shop.user.impl.logging;

import com.itmo.microservices.commonlib.logging.NotableEvent;
import org.jetbrains.annotations.NotNull;

public enum UserServiceNotableEvents implements NotableEvent {
    I_USER_CREATED("User created: {}");

    private final String user;

    UserServiceNotableEvents(String user) {
        this.user = user;
    }

    @NotNull
    @Override
    public String getName() {
        return this.user;
    }

    @NotNull
    @Override
    public String getTemplate() {
        return this.user;
    }
}
