package com.itmo.microservices.shop.catalog.api.service;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;

import java.util.List;

public interface IItemService {
    List<ItemDTO> getItems();
    List<ItemDTO> getAvailableItems();
}
