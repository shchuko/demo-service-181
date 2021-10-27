package com.itmo.microservices.shop.catalog.api.service;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.entity.Item;

import java.util.List;
import java.util.UUID;

public interface IItemService {
    List<ItemDTO> getItems();

    List<ItemDTO> getAvailableItems();

    int getCountOfItem(UUID uuid);

    void deleteItem(UUID uuid);

    void createItem(ItemDTO item);

}