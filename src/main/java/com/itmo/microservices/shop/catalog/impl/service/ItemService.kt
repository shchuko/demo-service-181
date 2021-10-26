package com.itmo.microservices.shop.catalog.impl.service;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.api.service.IItemService;
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ItemService implements IItemService {

    private final ItemRepository _itemRepository;

    @Autowired
    ItemService(ItemRepository itemRepository){
        _itemRepository = itemRepository;
    }

    @Override
    public List<ItemDTO> getItems() {
        //_itemRepository.findAll();
        return null;
    }

    @Override
    public List<ItemDTO> getAvailableItems() {
        return null;
    }
}
