package com.itmo.microservices.shop.catalog.common;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.entity.Item;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class HardcodedValues {

    public final UUID mockedUUID = UUID.fromString("8d8b30e3-de52-4f1c-a71c-9905a8043dac");

    public final List<Item> mockedItems = Arrays.asList(
            new Item(UUID.randomUUID(), "name1", 1, "desc1", 1),
            new Item(UUID.randomUUID(), "name2", 2, "desc2", 0)
    );
    public final List<ItemDTO> mockedItemsDto = fromEntityToDto(mockedItems);

    public final Item mockedItem = new Item(mockedUUID, "mocked", 999, "mocked", 666);
    public final ItemDTO mockedItemDTO = fromEntityToDto(mockedItem);

    public static List<ItemDTO> fromEntityToDto(List<Item> items) {
        List<ItemDTO> result = new LinkedList<>();
        items.forEach(item -> result.add(fromEntityToDto(item)));
        return result;
    }

    public static List<Item> fromDtoToEntity(List<ItemDTO> items) {
        List<Item> result = new LinkedList<>();
        items.forEach(item -> result.add(fromDtoToEntity(item)));
        return result;
    }

    public static ItemDTO fromEntityToDto(Item item) {
        return new ItemDTO(item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getAmount());
    }

    public static Item fromDtoToEntity(ItemDTO item) {
        return new Item(item.getId(), item.getName(), item.getPrice(), item.getDescription(), item.getAmount());
    }
}
