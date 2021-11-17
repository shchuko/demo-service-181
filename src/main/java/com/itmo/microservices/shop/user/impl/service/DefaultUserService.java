package com.itmo.microservices.shop.user.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.shop.common.exception.NotFoundException;
import com.itmo.microservices.shop.common.exception.UserExistsException;
import com.itmo.microservices.shop.user.api.messaging.UserCreatedEvent;
import com.itmo.microservices.shop.user.api.model.RegistrationRequest;
import com.itmo.microservices.shop.user.api.model.UserModel;
import com.itmo.microservices.shop.user.api.service.UserService;
import com.itmo.microservices.shop.user.impl.entity.User;
import com.itmo.microservices.shop.user.impl.logging.UserServiceNotableEvents;
import com.itmo.microservices.shop.user.impl.repository.UserRepository;
import com.itmo.microservices.shop.user.impl.util.UserEntityModelMappers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@SuppressWarnings("UnstableApiUsage")
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EventBus eventBus;

    @InjectEventLogger
    private EventLogger eventLogger;

    public DefaultUserService(UserRepository userRepository, EventBus eventBus) {
        this.userRepository = userRepository;
        this.eventBus = eventBus;
    }

    @Override
    public UserModel registerUser(RegistrationRequest request) {
        if (userRepository.existsUserByUsername(request.getUsername())) {
            if (eventLogger != null)
                eventLogger.error(UserServiceNotableEvents.E_USER_ALREADY_CREATED, request.getUsername());
            throw new UserExistsException("User already exists");
        }
        User user = userRepository.save(UserEntityModelMappers.toEntity(request, passwordEncoder));
        eventBus.post(new UserCreatedEvent(UserEntityModelMappers.toModel(user)));
        if (eventLogger != null)
            eventLogger.info(UserServiceNotableEvents.I_USER_CREATED, user.getUsername());

        return UserEntityModelMappers.toModel(user);
    }

    @Override
    public UserModel getUserByID(UUID uuid) {
        Optional<User> user = userRepository.findById(uuid);
        if (user.isEmpty()) {
            if (eventLogger != null)
                eventLogger.error(UserServiceNotableEvents.E_USER_NOT_FOUND_BY_UUID, uuid);
            throw new NotFoundException("User " + uuid + " not found");
        }
        if (eventLogger != null)
            eventLogger.info(UserServiceNotableEvents.I_USER_FOUND_BY_UUID, user.get().getUsername());
        return UserEntityModelMappers.toModel(user.get());
    }

    @Override
    public UserModel getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            if (eventLogger != null)
                eventLogger.error(UserServiceNotableEvents.E_USER_NOT_FOUND_BY_USERNAME, username);
            throw new NotFoundException("User " + username + " not found");
        }
        if (eventLogger != null)
            eventLogger.info(UserServiceNotableEvents.I_USER_FOUND_BY_NAME, user.getUsername());
        return UserEntityModelMappers.toModel(user);
    }
}
