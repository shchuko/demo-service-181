package com.itmo.microservices.shop.catalog.api.controller;

import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
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

    private ItemService itemService;

    @GetMapping()
    ResponseEntity<List<ItemDTO>> getAllItemsBasedOnAvailability(
            @RequestParam(value = "available", required = false) Boolean available) {
        if (available == null) {
            return new ResponseEntity<List<ItemDTO>>(itemService.getItems(), HttpStatus.OK);
        }
        if (available) {
            return new ResponseEntity<List<ItemDTO>>(itemService.getAvailableItems(), HttpStatus.OK);
        } else {
            return new ResponseEntity<List<ItemDTO>>(itemService.getNotAvailableItems(), HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<Integer> getCountOfItem(@PathVariable @DecimalMin("0") UUID id)
            throws ItemNotFoundException {
        return new ResponseEntity<>(itemService.getCountOfItem(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void updateItem(@PathVariable @DecimalMin("0") UUID id, @RequestBody ItemDTO itemDTO)
            throws ItemNotFoundException {
        itemService.updateItem(id, itemDTO);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    void addItem(@RequestBody ItemDTO item) {
        itemService.createItem(item);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void deleteItem(@PathVariable @DecimalMin("0") UUID id) {
        itemService.deleteItem(id);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public void handleItemNotFoundException(HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
