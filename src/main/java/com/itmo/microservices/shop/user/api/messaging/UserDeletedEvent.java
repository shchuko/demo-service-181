package com.itmo.microservices.shop.user.api.messaging;

public class UserDeletedEvent {
    private final String username;

    public UserDeletedEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
