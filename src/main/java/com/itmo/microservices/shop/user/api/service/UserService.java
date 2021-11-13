package com.itmo.microservices.shop.user.api.service;

import com.itmo.microservices.shop.user.api.model.RegistrationRequest;
import com.itmo.microservices.shop.user.api.model.UserModel;

import java.util.UUID;

public interface UserService {
    UserModel registerUser(RegistrationRequest request);

    UserModel getUserByID(UUID uuid);

    UserModel getUserByUsername(String username);
}
