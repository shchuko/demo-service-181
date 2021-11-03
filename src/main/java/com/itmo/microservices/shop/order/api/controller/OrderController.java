package com.itmo.microservices.shop.order.api.controller;

import com.itmo.microservices.shop.order.api.model.BookingDTO;
import com.itmo.microservices.shop.order.api.model.OrderDTO;
import com.itmo.microservices.shop.order.api.service.IOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<OrderDTO> createOrder() {
        return new ResponseEntity<>(orderService.createOrder(), HttpStatus.CREATED);
    }

    @GetMapping("/{order_id}")
    ResponseEntity<OrderDTO> getOrder(@PathVariable UUID order_id){
        return new ResponseEntity<>(orderService.getOrder(order_id), HttpStatus.OK);
    }

    @PutMapping(value = "/{order_id}/items/{item_id}", params = {"amount"})
    void addItem(@PathVariable UUID order_id, @PathVariable UUID item_id, @RequestParam int amount){
        orderService.addItem(order_id, item_id, amount);
    }

    @PostMapping(value = "/{order_id}/delivery", params = {"slot"})
    ResponseEntity<BookingDTO> setTime(@PathVariable UUID order_id, @RequestParam int slot){
        return new ResponseEntity<>(orderService.setTime(order_id, slot), HttpStatus.OK);
    }

    @PostMapping(value = "/{order_id}/bookings")
    ResponseEntity<BookingDTO> finalizeOrder(@PathVariable UUID order_id){
        return new ResponseEntity<>(orderService.finalizeOrder(order_id), HttpStatus.OK);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public void handleNoSuchElementException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
