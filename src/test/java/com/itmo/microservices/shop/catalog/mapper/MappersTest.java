package com.itmo.microservices.shop.catalog.mapper;

import com.itmo.microservices.shop.catalog.common.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.mapper.MappersKt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappersTest {
    private static final HardcodedValues HARDCODED_VALUES = new HardcodedValues();

    @Test
    public void whenConvertOneItem_thenGetItemDto() {
        var actual = MappersKt.mapToDTO(HARDCODED_VALUES.mockedItem);
        var expected = HardcodedValues.fromEntityToDto(HARDCODED_VALUES.mockedItem);
        assertEquals(expected, actual);
    }

    @Test
    public void whenConvertOneItemDTO_thenGetItem() {
        var expected = HardcodedValues.fromDtoToEntity(HARDCODED_VALUES.mockedItemDTO);
        var actual = MappersKt.mapToEntity(HARDCODED_VALUES.mockedItemDTO);
        assertEquals(expected, actual);
    }
}
