package com.itmo.microservices.shop.catalog.impl.service

import org.springframework.beans.factory.annotation.Autowired
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository
import com.itmo.microservices.shop.catalog.api.service.IItemService
import com.itmo.microservices.shop.catalog.api.model.ItemDTO
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class ItemService @Autowired constructor(private val itemRepository: ItemRepository) : IItemService {

    override fun getItems() = itemRepository.findAll()
        .stream()
        .map(ItemToItemDTOMapper::map)
        .collect(Collectors.toList())


    override fun getAvailableItems(): List<ItemDTO> {
        return items.filter { item ->
            item.count > 0
        }
    }
}