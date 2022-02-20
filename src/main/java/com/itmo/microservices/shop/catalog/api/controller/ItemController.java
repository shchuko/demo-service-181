package com.itmo.microservices.shop.catalog.api.controller;

import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.service.ItemServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.DecimalMin;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private ItemServiceImpl itemService;

    @GetMapping()
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<List<ItemDTO>> getAllItemsBasedOnAvailability(
            @RequestParam(value = "available", required = false) Boolean available) {
        if (available == null) {
            return new ResponseEntity<>(itemService.listItems(), HttpStatus.OK);
        }
        if (available) {
            return new ResponseEntity<>(itemService.listAvailableItems(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(itemService.listUnavailableItems(), HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<Integer> getCountOfItem(@PathVariable @DecimalMin("0") UUID id)
            throws ItemNotFoundException {
        return new ResponseEntity<>(itemService.getItemCount(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    void updateItem(@PathVariable @DecimalMin("0") UUID id, @RequestBody ItemDTO itemDTO)
            throws ItemNotFoundException {
        itemDTO.setId(id);
        itemService.updateItem(itemDTO);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    void addItem(@RequestBody ItemDTO item) {
        itemService.createItem(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    void deleteItem(@PathVariable @DecimalMin("0") UUID id) {
        itemService.deleteItem(id);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public void handleItemNotFoundException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}

