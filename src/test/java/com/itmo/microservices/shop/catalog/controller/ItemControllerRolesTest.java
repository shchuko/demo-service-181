package com.itmo.microservices.shop.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import com.itmo.microservices.shop.common.metrics.DemoServiceMetricsCollector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO: when role authorization will be done
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest()
public class ItemControllerRolesTest extends HardcodedValues {
    //Don't create an instance bcs when all tests start the instance had been created on the 1st test
    @MockBean
    private DemoServiceMetricsCollector demoServiceMetricsCollector;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    private final static ObjectMapper mapper = new ObjectMapper();

    private final static List<ItemDTO> availableItems = mockedItemsDto.stream()
            .filter(item -> item.getAmount() > 0).collect(Collectors.toList());


    @Test
    public void whenGetWithoutAuthentication_thenReturnForbiddenStatus() throws Exception {
        Mockito.when(itemService.getAvailableItems()).thenReturn(availableItems);
        Mockito.when(itemService.getItems()).thenReturn(mockedItemsDto);

        this.mockMvc.perform(get("/items")
                        .param("available", "true"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
