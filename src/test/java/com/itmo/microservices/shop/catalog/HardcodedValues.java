package com.itmo.microservices.shop.catalog;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.entity.Item;

import java.util.*;

public class HardcodedValues {

    protected static final UUID mockedUUID = UUID.fromString("8d8b30e3-de52-4f1c-a71c-9905a8043dac");

    protected static final List<Item> mockedItems = Arrays.asList(
            new Item(UUID.randomUUID(), "name1", 1, "desc1", 1),
            new Item(UUID.randomUUID(), "name2", 2, "desc2", 0)
    );
    protected static final List<ItemDTO> mockedItemsDto = fromEntityToDto(mockedItems);

    protected static final Item mockedItem = new Item(mockedUUID, "mocked", 999, "mocked", 666);
    protected static final ItemDTO mockedItemDTO = fromEntityToDto(mockedItem);

    protected static List<ItemDTO> fromEntityToDto(List<Item> items) {
        List<ItemDTO> result = new LinkedList<>();
        items.forEach(item -> result.add(fromEntityToDto(item)));
        return result;
    }

    protected static ItemDTO fromEntityToDto(Item item) {
        return new ItemDTO(item.getId(),item.getName(),item.getDescription(),item.getPrice(),item.getAmount());
    }
}
