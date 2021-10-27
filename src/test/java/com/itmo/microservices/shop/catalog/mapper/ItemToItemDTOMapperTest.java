package com.itmo.microservices.shop.catalog.mapper;

import com.itmo.microservices.shop.catalog.CatalogTest;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.stream.Collectors;

public class ItemToItemDTOMapperTest extends CatalogTest {
    @Test
    public void whenConvertOneItem_thenGetItemDto() {
        var test = ItemToItemDTOMapper.Companion.map(mockedItem);
        var correct = fromEntityToDto(mockedItem);
        Assertions.assertEquals(correct, test);
    }

    @Test
    public void whenConvertListOfItem_thenGetListOfItemDto() {
        var test = mockedItems.stream().map(ItemToItemDTOMapper.Companion::map).collect(Collectors.toList());
        var correct = fromEntityToDto(mockedItems);
        Assertions.assertEquals(correct, test);
    }
}
