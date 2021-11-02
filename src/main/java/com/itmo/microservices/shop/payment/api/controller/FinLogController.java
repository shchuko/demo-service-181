package com.itmo.microservices.shop.payment.api.controller;

import com.itmo.microservices.shop.payment.impl.service.FinLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<?> getUserFinancialLog(@RequestParam(defaultValue = "", required = false) String orderId) {
        UUID userId = UUID.fromString("e99b7ee6-ee5e-4cb9-9cdd-33fe50765e6e");
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
