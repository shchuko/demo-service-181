package com.itmo.microservices.shop.delivery.api.controller;

import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoModel;
import com.itmo.microservices.shop.delivery.api.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    DeliveryController(final DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/slots")
    @Operation(
            summary = "Get slots",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Slots not found", content = {@Content})
            },
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    public List<Integer> getTimeSlots(@RequestParam(defaultValue = "-1", required = false) int num) {
        if (num < 0) {
            return deliveryService.getDeliverySlots();
        }
        return deliveryService.getDeliverySlots(num);
    }

    @PostMapping("/set-time-slot")
    @Operation(
            summary = "Set time slot",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Doesn't set", content = {@Content})
            },
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    public DeliveryInfoModel setTimeSlot(@RequestBody DeliveryInfoModel deliveryInfoModel) {
        return deliveryService.setTimeSlot(deliveryInfoModel);
    }

    @PostMapping("/get-info/{orderId}")
    @Operation(
            summary = "Get info order Id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Couldn't find order Id", content = {@Content})
            },
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    public DeliveryInfoModel getDeliveryInfoByOrderId(@PathVariable UUID orderId) {
        return deliveryService.getDeliveryInfo(orderId);
    }
}
