package com.itmo.microservices.shop.catalog.mapper;

import com.itmo.microservices.shop.catalog.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

public class ItemToItemDTOMapperTest extends HardcodedValues {
    @Test
    public void whenConvertOneItem_thenGetItemDto() {
        var test = ItemToItemDTOMapper.Companion.map(mockedItem);
        var correct = fromEntityToDto(mockedItem);
        Assert.assertEquals(correct, test);
    }

    @Test
    public void whenConvertListOfItem_thenGetListOfItemDto() {
        var test = mockedItems.stream().map(ItemToItemDTOMapper.Companion::map).collect(Collectors.toList());
        var correct = fromEntityToDto(mockedItems);
        Assert.assertEquals(correct, test);
    }
}
