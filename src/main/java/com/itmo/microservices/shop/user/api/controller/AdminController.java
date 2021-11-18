package com.itmo.microservices.shop.user.api.controller;

import com.itmo.microservices.shop.user.api.exceptions.SecretIsIncorrectException;
import com.itmo.microservices.shop.user.api.exceptions.UserNotFoundException;
import com.itmo.microservices.shop.user.api.model.AdminDTO;
import com.itmo.microservices.shop.user.api.model.UpdateAdminDto;
import com.itmo.microservices.shop.user.impl.service.DefaultAdminService;
import com.itmo.microservices.shop.user.impl.userdetails.UserAuth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final DefaultAdminService adminService;

    AdminController(DefaultAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping()
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")},
            summary = "Checks if the current user is an administrator",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "403", description = "Bad authentication token", content = {@Content}),
                    @ApiResponse(responseCode = "404", description = "User not found", content = {@Content}),
            })
    ResponseEntity<AdminDTO> getInfoIsAdmin(Authentication auth) {
        var currentUser = (UserAuth) auth.getPrincipal();
        return new ResponseEntity<>(adminService.isIAdmin(currentUser.getUuid()), HttpStatus.OK);
    }

    @PutMapping()
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")},
            summary = "Changes administrator permissions for current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Secret is invalid", content = {@Content}),
                    @ApiResponse(responseCode = "403", description = "Bad authentication token", content = {@Content}),
                    @ApiResponse(responseCode = "404", description = "User not found", content = {@Content}),
            })
    ResponseEntity<AdminDTO> changeCurrentUserAdminAuthority(Authentication auth, @RequestBody UpdateAdminDto updateAdminDto) {
        UserAuth currentUser = (UserAuth) auth.getPrincipal();
        return new ResponseEntity<>(adminService.updateAdmin(updateAdminDto, currentUser.getUuid()), HttpStatus.OK);
    }

    //TODO: add global ControllerAdvice for all exceptions
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public void handleUserNotFoundException()
            throws IOException {
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SecretIsIncorrectException.class)
    public void handleSecretIsIncorrectException()
            throws IOException {
    }
}
