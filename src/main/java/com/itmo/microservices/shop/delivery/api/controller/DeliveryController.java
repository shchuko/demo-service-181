package com.itmo.microservices.shop.delivery.api.controller;

import com.itmo.microservices.shop.delivery.api.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery")
@AllArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @GetMapping("/slots")
    @Operation(
            summary = "Get slots",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Slots not found", content = {@Content})
            },
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    public List<Integer> getTimeSlots(@RequestParam(defaultValue = "-1", required = false) int number) {
        if (number < 0) {
            return deliveryService.getDeliverySlots();
        }
        return deliveryService.getDeliverySlots(number);
    }
}
