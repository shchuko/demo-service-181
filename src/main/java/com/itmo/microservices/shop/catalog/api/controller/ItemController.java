package com.itmo.microservices.shop.catalog.api.controller;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.entity.Item;
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService _itemService;

    @Autowired
    ItemController(ItemService itemService) {
        _itemService = itemService;
    }

    @GetMapping()
    ItemDTO getAllItems() {
        return null;
    }


}

