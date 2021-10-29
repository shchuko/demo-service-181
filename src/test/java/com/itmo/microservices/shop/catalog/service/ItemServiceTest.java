package com.itmo.microservices.shop.catalog.service;

import com.itmo.microservices.shop.catalog.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Collectors;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ItemServiceTest extends HardcodedValues {
    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Test
    public void whenGetItems_thenReturnAllItemsIncludesWithZeroAmount(){
        Mockito.when(itemRepository.findAll())
                .thenReturn(mockedItems);

        var test =  itemService.getItems();
        var correct = fromEntityToDto(mockedItems);
        Assert.assertEquals(correct, test);
    }

    @Test
    public void whenGetAvailableItems_thenReturnAllItemsWithoutZeroAmount() {
        var available = mockedItems.stream().filter(item -> item.getAmount() > 0).collect(Collectors.toList());
        Mockito.when(itemRepository.returnAvailableItems())
                .thenReturn(available);

        var test =  itemService.getAvailableItems();
        Assert.assertEquals(fromEntityToDto(available), test);
    }

    @Test
    public void whenGetCountOfExistedItem_thenReturnCount() {
        Mockito.when(itemRepository.getCount(mockedUUID))
                .thenReturn(mockedItem.getAmount());


        var test = itemService.getCountOfItem(mockedUUID);
        var correct = (int)mockedItem.getAmount();
        Assert.assertEquals(correct, test);
    }
}
