package com.itmo.microservices.shop.user.api.controller;

import com.itmo.microservices.shop.user.api.model.RegistrationRequest;
import com.itmo.microservices.shop.user.api.model.UserModel;
import com.itmo.microservices.shop.user.impl.service.DefaultUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final DefaultUserService userService;

    @PostMapping
    @Operation(
            summary = "Register new user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content})
            }
    )
    UserModel register(@RequestBody RegistrationRequest request) {
        return userService.registerUser(request);
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
}
