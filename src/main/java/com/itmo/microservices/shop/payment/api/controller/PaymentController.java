package com.itmo.microservices.shop.payment.api.controller;

import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.impl.service.DefaultPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class PaymentController {


    private final DefaultPaymentService paymentService;

    public PaymentController(DefaultPaymentService service) {
        this.paymentService = service;
    }

    // Connect to the Orders service
    @PostMapping("/{orderId}/payment")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<?> orderPayment(@PathVariable String orderId) {
        try {
            PaymentSubmissionDto paymentSubmissionDto = paymentService.orderPayment(orderId);
            return ResponseEntity.ok(paymentSubmissionDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
