package com.itmo.microservices.shop.user.impl.logging;

import com.itmo.microservices.commonlib.logging.NotableEvent;
import org.jetbrains.annotations.NotNull;

public enum AdminServiceNotableEvent implements NotableEvent {

    I_UPDATE_ADMIN_REQUEST("Requested change admin authority. {}"),
    I_IS_ADMIN_REQUEST("Requested check admin authority. {}"),

    E_USER_NOT_FOUND("User not found. UserUUID: {}"),
    E_ADMIN_SECRET_IS_INCORRECT("Admin secret is incorrect. Called UserUUID: {}");

    private final String template;

    AdminServiceNotableEvent(String template) {
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
