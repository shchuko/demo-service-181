package com.itmo.microservices.shop.catalog.impl.mapper

import com.itmo.microservices.shop.catalog.api.model.ItemDTO
import com.itmo.microservices.shop.catalog.impl.entity.Item
import org.springframework.beans.BeanUtils

class ItemDTOToItemMapper {

    companion object {
        fun map(data: ItemDTO): Item = Item().apply {
            BeanUtils.copyProperties(data, this, "id")
        }
    }
}