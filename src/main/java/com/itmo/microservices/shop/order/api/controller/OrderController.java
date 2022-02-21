package com.itmo.microservices.shop.order.api.controller;

import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException;
import com.itmo.microservices.shop.order.api.exeptions.NotEnoughPermissions;
import com.itmo.microservices.shop.order.api.exeptions.OrderNotFoundException;
import com.itmo.microservices.shop.order.api.exeptions.OrderServiceConflictException;
import com.itmo.microservices.shop.order.api.model.BookingDTO;
import com.itmo.microservices.shop.order.api.model.OrderDTO;
import com.itmo.microservices.shop.order.api.service.IOrderService;
import com.itmo.microservices.shop.user.impl.userdetails.UserAuth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<OrderDTO> createOrder(Authentication authentication) {
        var userId = ((UserAuth) authentication.getPrincipal()).getUuid();
        return new ResponseEntity<>(orderService.createOrder(userId), HttpStatus.CREATED);
    }

    @GetMapping("/{order_id}")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<OrderDTO> getOrder(Authentication authentication, @PathVariable UUID order_id) {
        var userId = ((UserAuth) authentication.getPrincipal()).getUuid();
        return new ResponseEntity<>(orderService.describeOrder(userId, order_id), HttpStatus.OK);
    }

    @PutMapping(value = "/{order_id}/items/{item_id}", params = {"amount"})
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    void addItem(Authentication authentication, @PathVariable UUID order_id, @PathVariable UUID item_id, @RequestParam int amount) {
        var userId = ((UserAuth) authentication.getPrincipal()).getUuid();
        orderService.addItem(userId, order_id, item_id, amount);
    }

    @PostMapping(value = "/{order_id}/delivery", params = {"slot"})
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<BookingDTO> setTime(Authentication authentication, @PathVariable UUID order_id, @RequestParam int slot) {
        var userId = ((UserAuth) authentication.getPrincipal()).getUuid();
        return new ResponseEntity<>(orderService.setTimeSlot(userId, order_id, slot), HttpStatus.OK);
    }

    @PostMapping(value = "/{order_id}/bookings")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<BookingDTO> finalizeOrder(Authentication authentication, @PathVariable UUID order_id) {
        var userId = ((UserAuth) authentication.getPrincipal()).getUuid();
        return new ResponseEntity<>(orderService.finalizeOrder(userId, order_id), HttpStatus.OK);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public void handleOrderNotFound(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(NotEnoughPermissions.class)
    public void handleNotEnoughPermissions(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public void handleItemNotFound(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(OrderServiceConflictException.class)
    public void handleOrderServiceConflictException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.CONFLICT.value());
    }
}
