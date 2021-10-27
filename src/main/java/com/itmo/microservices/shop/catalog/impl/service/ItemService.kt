package com.itmo.microservices.shop.catalog.impl.service

import com.itmo.microservices.shop.catalog.api.model.ItemDTO
import com.itmo.microservices.shop.catalog.api.service.IItemService
import com.itmo.microservices.shop.catalog.impl.entity.Item
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class ItemService(private val itemRepository: ItemRepository<Item>) : IItemService {

    override fun getItems(): MutableList<ItemDTO> = itemRepository.findAll()
        .stream()
        .map(ItemToItemDTOMapper::map)
        .collect(Collectors.toList())

    override fun getAvailableItems(): MutableList<ItemDTO> = itemRepository.returnAvailableItems().stream().map(ItemToItemDTOMapper::map).collect(Collectors.toList())

    override fun getCountOfItem(uuid: UUID): Int = itemRepository.getCount(uuid)

    override fun deleteItem(uuid: UUID) {
        itemRepository.delete(itemRepository.getById(uuid))
    }

    override fun createItem(itemDTO: ItemDTO) {
        val item = Item()
        BeanUtils.copyProperties(itemDTO, item, "uuid")
        itemRepository.save(item)
    }

    fun updateItem(uuid: UUID, itemDTO: ItemDTO) {
        val item = itemRepository.getById(uuid);
        BeanUtils.copyProperties(itemDTO, item, "uuid")
        itemRepository.save(item)
    }
}