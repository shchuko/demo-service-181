package com.itmo.microservices.shop.catalog.controller;

import com.itmo.microservices.shop.catalog.CatalogTest;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import com.itmo.microservices.shop.demo.users.api.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ItemControllerTest extends CatalogTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    //TODO: add mock for user service and get access token
    @Test
    public void ifRequestIsGetAndParamAvailableIsTrue_thenReturnItemsDTOWhereCountMoreThanZero() {
        Mockito.when(itemService.getAvailableItems())
                .thenReturn(mockedItemsDto.stream()
                        .filter(itemDTO -> itemDTO.getCount() > 0).collect(Collectors.toList())
                );
        Mockito.when(itemService.getItems())
                .thenReturn(mockedItemsDto);
        //mockMvc.perform(
    }
}
