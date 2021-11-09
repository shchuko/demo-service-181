package com.itmo.microservices.shop.user.impl.util;

import com.itmo.microservices.shop.user.api.model.RegistrationRequest;
import com.itmo.microservices.shop.user.api.model.UserModel;
import com.itmo.microservices.shop.user.impl.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;


public class UserEntityModelMappers {
    public static UserModel toModel(User user) {
        return new UserModel(user.getId(), user.getUsername(), user.getPasswordHash(), user.getIsAdmin());
    }

    public static User toEntity(RegistrationRequest request, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        return user;
    }
}
