package com.itmo.microservices.shop.user.api.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthenticationResult {

    private String accessToken;
    private String refreshToken;
    private UUID uuid;
}
