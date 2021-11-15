package com.itmo.microservices.shop.user.util;

import com.itmo.microservices.shop.user.api.model.RegistrationRequest;
import com.itmo.microservices.shop.user.api.model.UserModel;
import com.itmo.microservices.shop.user.impl.entity.User;
import com.itmo.microservices.shop.user.impl.util.UserEntityModelMappers;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserUtilTest {
    private final UUID uuid = UUID.randomUUID();
    private final String username = "Test";
    private final String password = "qwerty";
    private final Boolean isAdmin = false;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void canConvertToEntity() {
        User except = UserEntityModelMappers.toEntity(createDefaultRequest(), passwordEncoder);

        assertEquals(username, except.getUsername());
        assertTrue(passwordEncoder.matches(password, except.getPasswordHash()));
    }

    @Test
    void canConvertToModel() {
        UserModel except = UserEntityModelMappers.toModel(createDefaultEntity());

        assertEquals(uuid, except.getUuid());
        assertEquals(username, except.getUsername());
        assertTrue(passwordEncoder.matches(password, except.getPassword()));
        assertEquals(isAdmin, except.isAdmin());
    }


    private User createDefaultEntity() {
        User user = new User();
        user.setId(uuid);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setIsAdmin(isAdmin);
        return user;
    }

    private RegistrationRequest createDefaultRequest() {
        return new RegistrationRequest(username, password);
    }
}
