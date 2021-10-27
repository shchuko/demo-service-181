package com.itmo.microservices.shop.catalog;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.entity.Item;
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper;

import java.util.*;
import java.util.stream.Collectors;

public class CatalogTest {
    protected final List<Item> mockedItems = Arrays.asList(
            new Item(UUID.randomUUID(), "name1", 1, "desc1", 1),
            new Item(UUID.randomUUID(), "name2", 2, "desc2", 0)
    );

    protected final UUID mockedUUID = UUID.fromString("8d8b30e3-de52-4f1c-a71c-9905a8043dac");

    protected final List<ItemDTO> mockedItemsDto = fromEntityToDto(mockedItems);

    protected final Item mockedItem = new Item(mockedUUID, "mocked", 999, "mocked", 666);

    protected List<ItemDTO> fromEntityToDto(List<Item> items) {
        List<ItemDTO> result = new LinkedList<>();
        items.forEach(item -> result.add(fromEntityToDto(item)));
        return result;
    }

    protected ItemDTO fromEntityToDto(Item item) {
        return new ItemDTO(item.getUuid(),item.getName(),item.getDescription(),item.getPrice(),item.getCount());
    }
}
