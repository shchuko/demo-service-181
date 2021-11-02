package com.itmo.microservices.shop.catalog.mapper;

import com.itmo.microservices.shop.catalog.common.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

public class ItemToItemDTOMapperTest {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Test
    public void whenConvertOneItem_thenGetItemDto() {
        var test = ItemToItemDTOMapper.Companion.map(hardcodedValues.mockedItem);
        var correct = HardcodedValues.fromEntityToDto(hardcodedValues.mockedItem);

        Assertions.assertEquals(correct, test);
    }

    @Test
    public void whenConvertListOfItem_thenGetListOfItemDto() {
        var test = hardcodedValues.mockedItems.stream().map(ItemToItemDTOMapper.Companion::map).collect(Collectors.toList());
        var correct = HardcodedValues.fromEntityToDto(hardcodedValues.mockedItems);

        Assertions.assertEquals(correct, test);
    }
}
