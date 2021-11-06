package com.itmo.microservices.shop.catalog.api.service;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;

import java.util.List;
import java.util.UUID;

public interface ItemService {
    List<ItemDTO> getItems();

    List<ItemDTO> getAvailableItems();

    List<ItemDTO> getUnavailableItems();

    int getCountOfItem(UUID uuid);

    void deleteItem(UUID uuid);

    void createItem(ItemDTO item);

    void updateItem(UUID uuid, ItemDTO itemDTO);
}
