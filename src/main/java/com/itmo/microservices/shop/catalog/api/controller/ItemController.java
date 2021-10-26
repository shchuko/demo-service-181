package com.itmo.microservices.shop.catalog.api.controller;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

  private ItemService itemService ;


  @GetMapping()
  ResponseEntity<List<ItemDTO>> getAllItems(@RequestParam(value = "available") boolean available) {
    return available ? new ResponseEntity<List<ItemDTO>>(itemService.getAvailableItems(),
        HttpStatus.OK) : new ResponseEntity<List<ItemDTO>>(itemService.getItems(), HttpStatus.OK);
  }


}

