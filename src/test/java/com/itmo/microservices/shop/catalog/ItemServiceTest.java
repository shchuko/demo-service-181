package com.itmo.microservices.shop.catalog;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.impl.entity.Item;
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper;
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ItemServiceTest {
    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    private final List<Item> mockedItems = Arrays.asList(
            new Item(UUID.randomUUID(), "name1", 1, "desc1", 1),
            new Item(UUID.randomUUID(), "name1", 2, "desc2", 0)
    );


    @Test
    public void whenGetItems_thenReturnAllItemsIncludesWithZeroAmount(){
        Mockito.when(itemRepository.findAll())
                .thenReturn(mockedItems);

        var test =  itemService.getItems();
        var correct = fromEntityToDto(mockedItems);
        Assertions.assertEquals(correct, test);
    }

    @Test
    public void whenGetAvailableItems_thenReturnAllItemsWithoutZeroAmount() {
        var available = mockedItems.stream().filter(item -> item.getCount() > 0).collect(Collectors.toList());
        Mockito.when(itemRepository.returnAvailableItems())
                .thenReturn(available);

        var test =  itemService.getAvailableItems();
        Assertions.assertEquals(fromEntityToDto(available), test);
    }

    @Test
    public void whenGetCountOfExistedItem_thenReturnCount() {
        UUID testUUID = UUID.randomUUID();
        Item testItem = new Item(testUUID, "name1", 1, "desc1", 1);
        Mockito.when(itemRepository.getCount(testUUID))
                .thenReturn(testItem.getCount());


        var test = itemService.getCountOfItem(testUUID);
        var correct = testItem.getCount();
        Assertions.assertEquals(correct, test);
    }

    private List<ItemDTO> fromEntityToDto(List<Item> items) {
        return items.stream().map(ItemToItemDTOMapper.Companion::map).collect(Collectors.toList());
    }

}
