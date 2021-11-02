package com.itmo.microservices.shop.catalog.mapper;

import com.itmo.microservices.shop.catalog.common.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.mapper.ItemDTOToItemMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

public class ItemDTOToItemMapperTest {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Test
    public void whenConvertOneItemDTO_thenGetItemWithoutId() {
        var test = ItemDTOToItemMapper.Companion.map(hardcodedValues.mockedItemDTO);
        var correct = HardcodedValues.fromDtoToEntity(hardcodedValues.mockedItemDTO);

        Assertions.assertEquals(correct, test);
    }

    @Test
    public void whenConvertListOfItemDTO_thenGetListOfItemWithoutId() {
        var test = hardcodedValues.mockedItemsDto.stream().map(ItemDTOToItemMapper.Companion::map).collect(Collectors.toList());
        var correct = hardcodedValues.fromDtoToEntity(hardcodedValues.mockedItemsDto);

        Assertions.assertEquals(correct, test);
    }
}
