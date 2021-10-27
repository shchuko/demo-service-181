package com.itmo.microservices.shop.catalog.api.controller;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import java.util.UUID;
import javax.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

  private ItemService itemService;

  @GetMapping()
  ResponseEntity<List<ItemDTO>> getAllItems(@RequestParam(value = "available") boolean available) {
    return available ? new ResponseEntity<List<ItemDTO>>(itemService.getAvailableItems(),
        HttpStatus.OK) : new ResponseEntity<List<ItemDTO>>(itemService.getItems(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  ResponseEntity<Integer> getItem(@PathVariable @DecimalMin("0") UUID id) {
    return new ResponseEntity<>(itemService.getCountOfItem(id), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  void updateItem(@PathVariable @DecimalMin("0") UUID id, @RequestBody ItemDTO itemDTO){
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

}

