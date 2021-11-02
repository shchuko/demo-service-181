package com.itmo.microservices.shop.catalog.impl.service

import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException
import com.itmo.microservices.shop.catalog.api.model.ItemDTO
import com.itmo.microservices.shop.catalog.api.service.ItemService
import com.itmo.microservices.shop.catalog.impl.mapper.ItemDTOToItemMapper
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class ItemService(private val itemRepository: ItemRepository) :
    ItemService {

    override fun getItems(): MutableList<ItemDTO> = itemRepository.findAll()
        .stream()
        .map(ItemToItemDTOMapper::map)
        .collect(Collectors.toList())

    override fun getAvailableItems(): MutableList<ItemDTO> =
        itemRepository.findAllByAmountGreaterThan(0)
            .stream()
            .map(ItemToItemDTOMapper::map)
            .collect(Collectors.toList())

    override fun getNotAvailableItems(): MutableList<ItemDTO> =
        itemRepository.findAllByAmountLessThanEqual(0)
            .stream()
            .map(ItemToItemDTOMapper::map)
            .collect(Collectors.toList())

    @Throws(ItemNotFoundException::class)
    override fun getCountOfItem(uuid: UUID): Int = itemRepository.findById(uuid).let {
        if (it.isEmpty) {
            throw ItemNotFoundException(" No value with $uuid exists");
        } else {
            it.get().amount
        }
    }

    override fun deleteItem(uuid: UUID) {
        itemRepository.deleteById(uuid)
    }

    override fun createItem(itemDTO: ItemDTO) {
        itemRepository.save(ItemDTOToItemMapper.map(itemDTO))
    }

    @Throws(ItemNotFoundException::class)
    override fun updateItem(uuid: UUID, itemDTO: ItemDTO) {
        val item = itemRepository.findById(uuid).let {
            if (it.isEmpty) {
                throw ItemNotFoundException(" No value with $uuid exists");
            } else {
                it.get()
            }
        }

        item.apply {
            BeanUtils.copyProperties(itemDTO, this, "uuid")
            itemRepository.save(this)
        }
    }
}