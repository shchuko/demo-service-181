package com.itmo.microservices.shop.catalog.service;

import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException;
import com.itmo.microservices.shop.catalog.common.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository;
import com.itmo.microservices.shop.catalog.impl.service.ItemServiceImpl;
import com.itmo.microservices.shop.common.test.DefaultSecurityTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.stream.Collectors;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class ItemServiceImplTest extends DefaultSecurityTestCase {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemServiceImpl itemService;

    @Test
    public void whenGetItems_thenReturnAllItemsIncludesWithZeroAmount() {
        Mockito.when(itemRepository.findAll())
                .thenReturn(hardcodedValues.mockedItems);

        var test = itemService.listItems();
        var correct = HardcodedValues.fromEntityToDto(hardcodedValues.mockedItems);

        Assertions.assertEquals(correct, test);

        Mockito.verify(itemRepository).findAll();
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void whenGetAvailableItems_thenReturnAllItemsWithoutZeroAmount() {
        var available = hardcodedValues.mockedItems.stream().filter(item -> item.getAmount() > 0).collect(Collectors.toList());

        Mockito.when(itemRepository.findAllByAmountGreaterThan(0))
                .thenReturn(available);

        var test = itemService.listAvailableItems();
        var correct = HardcodedValues.fromEntityToDto(available);

        Assertions.assertEquals(correct, test);

        Mockito.verify(itemRepository).findAllByAmountGreaterThan(Mockito.eq(0));
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void whenGetCountOfExistedItem_thenReturnCount() {
        Mockito.when(itemRepository.findById(hardcodedValues.mockedUUID))
                .thenReturn(Optional.of(hardcodedValues.mockedItem));

        var test = itemService.getItemCount(hardcodedValues.mockedUUID);
        var correct = (int) hardcodedValues.mockedItem.getAmount();

        Assertions.assertEquals(correct, test);

        Mockito.verify(itemRepository).findById(Mockito.eq(hardcodedValues.mockedUUID));
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void whenGetCountOfNotExistedItem_thenReturnCount() {
        Mockito.when(itemRepository.findById(hardcodedValues.mockedUUID))
                .thenReturn(Optional.empty());

        try {
            itemService.getItemCount(hardcodedValues.mockedUUID);
            Assertions.fail("exception wasn't throw");
        } catch (Exception test) {
            Assertions.assertEquals(ItemNotFoundException.class, test.getClass(), "throw class is wrong");
        }

        Mockito.verify(itemRepository).findById(Mockito.eq(hardcodedValues.mockedUUID));
        Mockito.verifyNoMoreInteractions(itemRepository);
    }
}
