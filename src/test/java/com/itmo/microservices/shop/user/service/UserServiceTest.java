package com.itmo.microservices.shop.user.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.shop.common.exception.UserExistsException;
import com.itmo.microservices.shop.user.api.model.RegistrationRequest;
import com.itmo.microservices.shop.user.api.model.UserModel;
import com.itmo.microservices.shop.user.impl.entity.User;
import com.itmo.microservices.shop.user.impl.repository.UserRepository;
import com.itmo.microservices.shop.user.impl.service.DefaultUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
public class UserServiceTest {
    private UserRepository repository;
    private DefaultUserService service;

    private final UUID uuid = UUID.randomUUID();
    private final String username = "Test";
    private final String password = "qwerty";
    private final Boolean isAdmin = false;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void init() {
        repository = Mockito.mock(UserRepository.class);
        Mockito.doReturn(createDefaultUser()).when(repository).findByUsername(username);

        service = new DefaultUserService(repository, new EventBus());
    }

    @Test
    void canCreateUser() {
        Mockito.when(repository.save(Mockito.any())).then(a -> {
            assertNotNull(a.getArgument(0));
            User user = a.getArgument(0);
            assertEquals(username, user.getUsername());
            assertTrue(passwordEncoder.matches(password, user.getPasswordHash()));
            return user;
        });

        try {
            service.registerUser(createDefaultRequest());
        } catch (UserExistsException ignored) {
        }
    }

    @Test
    void canGetUserByUsername() {
        Mockito.when(repository.findByUsername(username)).then(a -> {
            assertNotNull(a.getArgument(0));
            return createDefaultUser();
        });

        UserModel userModel = service.getUserByUsername("Test");

        assertEquals(uuid, userModel.getUuid());
        assertEquals(username, userModel.getUsername());
        assertTrue(passwordEncoder.matches(password, userModel.getPassword()));
        assertEquals(isAdmin, userModel.isAdmin());
    }

    @Test
    void canGetUserById() {
        Mockito.when(repository.findById(uuid)).then(a -> {
            assertNotNull(a.getArgument(0));
            return Optional.of(createDefaultUser());
        });

        UserModel userModel = service.getUserByID(uuid);

        assertEquals(uuid, userModel.getUuid());
        assertEquals(username, userModel.getUsername());
        assertTrue(passwordEncoder.matches(password, userModel.getPassword()));
        assertEquals(isAdmin, userModel.isAdmin());
    }

    private User createDefaultUser() {
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
