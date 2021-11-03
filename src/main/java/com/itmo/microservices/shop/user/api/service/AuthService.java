package com.itmo.microservices.shop.user.api.service;

import com.itmo.microservices.shop.user.api.model.AuthenticationRequest;
import com.itmo.microservices.shop.user.api.model.AuthenticationResult;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthenticationResult authenticate(AuthenticationRequest request);

    AuthenticationResult refresh(Authentication authentication);
}
