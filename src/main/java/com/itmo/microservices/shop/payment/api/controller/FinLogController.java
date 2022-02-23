package com.itmo.microservices.shop.payment.api.controller;

import com.itmo.microservices.shop.payment.api.service.PaymentService;
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

    private final PaymentService paymentService;

    public FinLogController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<?> getUserFinancialLog(Authentication authentication, @RequestParam(defaultValue = "", required = false) UUID orderId) {
        var user = (UserAuth) authentication.getPrincipal();
        UUID userId = user.getUuid();
        try {
            if (orderId == null) {
                return ResponseEntity.ok(paymentService.listUserFinLog(userId));
            } else {
                return ResponseEntity.ok(paymentService.listUserFinLog(userId, orderId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}
