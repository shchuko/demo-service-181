package com.itmo.microservices.shop.user.impl.logging;

import com.itmo.microservices.commonlib.logging.NotableEvent;
import org.jetbrains.annotations.NotNull;

public enum AuthServiceNotableEvents implements NotableEvent {
    I_ACCESS_TOKEN_CREATED("Access token for user created: {}"),
    I_REFRESH_TOKEN_CREATED("Refresh token for user created: {}"),
    E_ACCESS_TOKEN_CREATION_FAILED("Cannot create access token for user: {}"),
    E_REFRESH_TOKEN_CREATION_FAILED("Cannot create access token for user: {}"),
    E_INVALID_PASSWORD("Invalid password: {}"),
    E_USER_NOT_FOUND_BY_USERNAME("User not found by username : {}");

    private final String template;

    AuthServiceNotableEvents(String template) {
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
