package com.itmo.microservices.shop.catalog.impl.mapper

import com.itmo.microservices.shop.catalog.api.model.ItemDTO
import com.itmo.microservices.shop.catalog.impl.entity.Item

class ItemToItemDTOMapper {

    companion object {
        fun map(data: Item) = ItemDTO(
            data.uuid,
            data.name,
            data.description,
            data.price,
            data.count
        )
    }
}