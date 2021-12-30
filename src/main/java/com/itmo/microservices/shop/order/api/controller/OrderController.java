package com.itmo.microservices.shop.order.api.controller;

import com.itmo.microservices.shop.order.api.exeptions.InvalidItemException;
import com.itmo.microservices.shop.order.api.exeptions.OrderAlreadyBookedException;
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
import java.util.NoSuchElementException;
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
        var auth = (UserAuth) authentication.getPrincipal();
        return new ResponseEntity<>(orderService.createOrder(auth.getUuid()), HttpStatus.CREATED);
    }

    @GetMapping("/{order_id}")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<OrderDTO> getOrder(@PathVariable UUID order_id){
        return new ResponseEntity<>(orderService.getOrder(order_id), HttpStatus.OK);
    }

    @PutMapping(value = "/{order_id}/items/{item_id}", params = {"amount"})
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    void addItem(@PathVariable UUID order_id, @PathVariable UUID item_id, @RequestParam int amount){
        orderService.addItem(order_id, item_id, amount);
    }

    @PostMapping(value = "/{order_id}/delivery", params = {"slot"})
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<BookingDTO> setTime(@PathVariable UUID order_id, @RequestParam int slot){
        return new ResponseEntity<>(orderService.setTime(order_id, slot), HttpStatus.OK);
    }

    @PostMapping(value = "/{order_id}/bookings")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<BookingDTO> finalizeOrder(@PathVariable UUID order_id){
        return new ResponseEntity<>(orderService.finalizeOrder(order_id), HttpStatus.OK);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public void handleNoSuchElementException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(OrderAlreadyBookedException.class)
    public void handleOrderAlreadyBookedException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(InvalidItemException.class)
    public void handleInvalidItemException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
