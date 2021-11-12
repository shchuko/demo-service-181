package com.itmo.microservices.shop.payment.api.controller;

import com.itmo.microservices.shop.payment.impl.service.FinLogService;
import com.itmo.microservices.shop.user.impl.userdetails.UserAuth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/finlog")
public class FinLogController {

    private final FinLogService finLogService;

    public FinLogController(FinLogService service) {
        finLogService = service;
    }

    // Add userId
    @GetMapping
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<?> getUserFinancialLog(Authentication authentication, @RequestParam(defaultValue = "", required = false) String orderId) {
        var user = (UserAuth) authentication.getPrincipal();
        UUID userId = user.getUuid();
        try {
            if (orderId.isEmpty()) {
                return ResponseEntity.ok(finLogService.getUserFinanceLog(userId));
            } else {
                return ResponseEntity.ok(finLogService.getUserFinanceLog(userId, orderId));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
