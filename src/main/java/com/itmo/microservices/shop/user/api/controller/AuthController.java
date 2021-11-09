package com.itmo.microservices.shop.user.api.controller;

import com.itmo.microservices.shop.user.api.model.AuthenticationRequest;
import com.itmo.microservices.shop.user.api.model.AuthenticationResult;
import com.itmo.microservices.shop.user.impl.service.DefaultAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@AllArgsConstructor
public class AuthController {

    private final DefaultAuthService authService;

    @PostMapping("")
    @Operation(
            summary = "Authenticate",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = {@Content}),
                    @ApiResponse(responseCode = "403", description = "Invalid password", content = {@Content})
            }
    )
    AuthenticationResult authenticate(@RequestBody AuthenticationRequest authRequest) {
        return authService.authenticate(authRequest);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh authentication",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "403", description = "Authentication error", content = {
                            @Content})
            },
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    AuthenticationResult refresh(Authentication authentication) {
        return authService.refresh(authentication);
    }
}
