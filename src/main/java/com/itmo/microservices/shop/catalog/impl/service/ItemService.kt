package com.itmo.microservices.shop.catalog.impl.service

import com.itmo.microservices.shop.catalog.api.model.ItemDTO
import com.itmo.microservices.shop.catalog.api.service.IItemService
import com.itmo.microservices.shop.catalog.impl.entity.Item
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class ItemService(private val itemRepository: ItemRepository) : IItemService {

    override fun getItems() = itemRepository.findAll()
        .stream()
        .map(ItemToItemDTOMapper::map)
        .collect(Collectors.toList())


    override fun getAvailableItems(): List<ItemDTO> {
        return items.filter { item ->
            item.count > 0
        }
    }

    override fun getCountOfItem(id: UUID) = itemRepository.getById(id).count

    override fun deleteItem(uuid: UUID) {
        itemRepository.delete(itemRepository.getById(uuid))
    }

    override fun createItem(item: Item) {
        itemRepository.save(item)
    }

    override fun updateItem(item: Item) {
        itemRepository.save(changeParams(itemRepository.getById(item.uuid), item))
    }

    private fun changeParams(sourceItem: Item, newItem: Item) =
        Item(sourceItem.uuid, newItem.name, newItem.price, newItem.description, newItem.count)
}