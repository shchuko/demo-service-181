package com.itmo.microservices.shop.user.impl.service;

import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.shop.common.exception.AccessDeniedException;
import com.itmo.microservices.shop.common.exception.NotFoundException;
import com.itmo.microservices.shop.user.api.model.AuthenticationRequest;
import com.itmo.microservices.shop.user.api.model.AuthenticationResult;
import com.itmo.microservices.shop.user.api.model.UserModel;
import com.itmo.microservices.shop.user.api.service.AuthService;
import com.itmo.microservices.shop.user.api.service.UserService;
import com.itmo.microservices.shop.user.impl.logging.AuthServiceNotableEvents;
import com.itmo.microservices.shop.user.impl.userdetails.UserAuth;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DefaultAuthService implements AuthService {
    private final UserService userService;
    private final JwtTokenManager tokenManager;
    private final PasswordEncoder passwordEncoder;

    @InjectEventLogger
    private EventLogger eventLogger;

    public DefaultAuthService(UserService userService, JwtTokenManager tokenManager, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenManager = tokenManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResult authenticate(AuthenticationRequest request) {
        UserModel user = userService.getUserByUsername(request.getUsername());
        if (user == null) {
            if (eventLogger != null) {
                eventLogger.error(AuthServiceNotableEvents.E_USER_NOT_FOUND_BY_USERNAME, request.getUsername());
                eventLogger.error(AuthServiceNotableEvents.E_ACCESS_TOKEN_CREATION_FAILED, request.getUsername());
                eventLogger.error(AuthServiceNotableEvents.E_REFRESH_TOKEN_CREATION_FAILED, request.getUsername());
            }
            throw new NotFoundException("User with username " + request.getUsername() + " not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            if (eventLogger != null) {
                eventLogger.error(AuthServiceNotableEvents.E_INVALID_PASSWORD, request.getPassword());
                eventLogger.error(AuthServiceNotableEvents.E_ACCESS_TOKEN_CREATION_FAILED, request.getUsername());
                eventLogger.error(AuthServiceNotableEvents.E_REFRESH_TOKEN_CREATION_FAILED, request.getUsername());
            }
            throw new AccessDeniedException("Invalid password");
        }

        String accessToken = tokenManager.generateToken(user.userDetails());
        String refreshToken = tokenManager.generateRefreshToken(user.userDetails());

        if (eventLogger != null) {
            eventLogger.info(AuthServiceNotableEvents.I_ACCESS_TOKEN_CREATED, user.getUsername());
            eventLogger.info(AuthServiceNotableEvents.I_REFRESH_TOKEN_CREATED, user.getUsername());
        }

        return new AuthenticationResult(accessToken, refreshToken, user.getUuid());
    }

    @Override
    public AuthenticationResult refresh(Authentication authentication) {
        String refreshToken = (String) authentication.getCredentials();
        UserAuth principal = (UserAuth) authentication.getPrincipal();
        String accessToken = tokenManager.generateToken(principal);

        if (eventLogger != null) {
            eventLogger.info(AuthServiceNotableEvents.I_ACCESS_TOKEN_CREATED, principal.getUsername());
        }

        return new AuthenticationResult(accessToken, refreshToken, principal.getUuid());
    }
}
