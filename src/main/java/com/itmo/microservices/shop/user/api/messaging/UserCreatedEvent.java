package com.itmo.microservices.shop.user.api.messaging;

import com.itmo.microservices.shop.user.api.model.UserModel;

public class UserCreatedEvent {
    private final UserModel userModel;

    public UserCreatedEvent(UserModel userModel) {
        this.userModel = userModel;
    }

    public UserModel getUserModel() {
        return userModel;
    }
}
