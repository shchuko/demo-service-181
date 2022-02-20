package com.itmo.microservices.shop.payment.api.controller;

import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.api.service.PaymentService;
import com.itmo.microservices.shop.user.impl.userdetails.UserAuth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService service) {
        this.paymentService = service;
    }

    // Connect to the Orders service
    @PostMapping("/{orderId}/payment")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<?> orderPayment(Authentication authentication, @PathVariable UUID orderId) {
        var user = (UserAuth) authentication.getPrincipal();
        UUID userId = user.getUuid();

        try {
            PaymentSubmissionDto paymentSubmissionDto = paymentService.payForOrder(userId, orderId);
            return ResponseEntity.ok(paymentSubmissionDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
