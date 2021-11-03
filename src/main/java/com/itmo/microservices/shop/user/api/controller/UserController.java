package com.itmo.microservices.shop.user.api.controller;

import com.itmo.microservices.shop.user.api.model.AuthenticationRequest;
import com.itmo.microservices.shop.user.api.model.AuthenticationResult;
import com.itmo.microservices.shop.user.api.model.RegistrationRequest;
import com.itmo.microservices.shop.user.api.model.UserModel;
import com.itmo.microservices.shop.user.impl.service.DefaultAuthService;
import com.itmo.microservices.shop.user.impl.service.DefaultUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final DefaultUserService userService;
    private final DefaultAuthService authService;

    public UserController(DefaultUserService userService, DefaultAuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping
    @Operation(
            summary = "Register new user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content})
            }
    )
    void register(@RequestBody RegistrationRequest request) {
        userService.registerUser(request);
    }

    @GetMapping("{user_id}")
    @Operation(
            summary = "Get user by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = {@Content})
            },
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    UserModel getAccountData(@PathVariable UUID user_id) {
        return userService.getUserByID(user_id);
    }

    @PostMapping("/authentication")
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
                    @ApiResponse(responseCode = "403", description = "Authentication error", content = {@Content})
            },
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    AuthenticationResult refresh(Authentication authentication) {
        return authService.refresh(authentication);
    }
}
