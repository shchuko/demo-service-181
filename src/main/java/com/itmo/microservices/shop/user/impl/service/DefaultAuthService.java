package com.itmo.microservices.shop.user.impl.service;

import com.itmo.microservices.shop.common.exception.AccessDeniedException;
import com.itmo.microservices.shop.common.exception.NotFoundException;
import com.itmo.microservices.shop.user.api.model.AuthenticationRequest;
import com.itmo.microservices.shop.user.api.model.AuthenticationResult;
import com.itmo.microservices.shop.user.api.model.UserModel;
import com.itmo.microservices.shop.user.api.service.AuthService;
import com.itmo.microservices.shop.user.api.service.UserService;
import com.itmo.microservices.shop.user.impl.userdetails.UserAuth;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DefaultAuthService implements AuthService {
    private final UserService userService;
    private final JwtTokenManager tokenManager;
    private final PasswordEncoder passwordEncoder;

    public DefaultAuthService(UserService userService, JwtTokenManager tokenManager, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenManager = tokenManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResult authenticate(AuthenticationRequest request) {
        UserModel user = userService.getUserByUsername(request.getUsername());
        if (user == null)
            throw new NotFoundException("User with username " + request.getUsername() + " not found");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new AccessDeniedException("Invalid password");

        String accessToken = tokenManager.generateToken(user.userDetails());
        String refreshToken = tokenManager.generateRefreshToken(user.userDetails());
        return new AuthenticationResult(accessToken, refreshToken, user.getUuid());
    }

    @Override
    public AuthenticationResult refresh(Authentication authentication) {
        String refreshToken = (String) authentication.getCredentials();
        UserAuth principal = (UserAuth) authentication.getPrincipal();
        String accessToken = tokenManager.generateToken(principal);
        return new AuthenticationResult(accessToken, refreshToken, principal.getUuid());
    }
}
