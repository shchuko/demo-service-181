package com.itmo.microservices.shop.delivery.api.controller;

import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoRecordDto;
import com.itmo.microservices.shop.delivery.api.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/_internal")
@AllArgsConstructor
public class InternalDeliveryController {
    private final DeliveryService deliveryService;

    @GetMapping("/deliveryLog")
    @Operation(
            summary = "Get delivery log",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Slots not found", content = {@Content})
            },
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    public List<DeliveryInfoRecordDto> getDeliveryLog(@RequestParam(required = true) UUID orderId) {
        return deliveryService.getDeliveryLog(orderId);
    }
}
