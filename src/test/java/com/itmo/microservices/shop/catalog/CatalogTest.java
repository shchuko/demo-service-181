package com.itmo.microservices.shop.catalog;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.entity.Item;
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CatalogTest {
    protected final List<Item> mockedItems = Arrays.asList(
            new Item(UUID.randomUUID(), "name1", 1, "desc1", 1),
            new Item(UUID.randomUUID(), "name1", 2, "desc2", 0)
    );

    protected final UUID mockedUUID = UUID.randomUUID();

    protected final Item mockedItem = new Item(mockedUUID, "mocked", 999, "mocked", 666);

    protected List<ItemDTO> fromEntityToDto(List<Item> items) {
        return items.stream().map(ItemToItemDTOMapper.Companion::map).collect(Collectors.toList());
    }
}
