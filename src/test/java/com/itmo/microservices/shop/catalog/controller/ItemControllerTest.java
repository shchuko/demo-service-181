package com.itmo.microservices.shop.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.catalog.HardcodedValues;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.config.NoSecurityConfigurerAdapterConfig;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import com.itmo.microservices.shop.common.metrics.DemoServiceMetricsCollector;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = NoSecurityConfigurerAdapterConfig.class)
public class ItemControllerTest extends HardcodedValues {
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
    public void whenGet_ParamAvailableTrue_thenReturnItemsWithCountMoreThanZero() throws Exception {
        Mockito.when(itemService.getAvailableItems()).thenReturn(availableItems);
        Mockito.when(itemService.getItems()).thenReturn(mockedItemsDto);

        final String expectedResponseContent = mapper.writeValueAsString(availableItems);

        this.mockMvc.perform(get("/items")
                        .param("available", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseContent));
    }

    @Test
    public void whenGet_ParamAvailableFalse_thenReturnAllItems() throws Exception {
        Mockito.when(itemService.getAvailableItems()).thenReturn(availableItems);
        Mockito.when(itemService.getItems()).thenReturn(mockedItemsDto);

        final String expectedResponseContent = mapper.writeValueAsString(mockedItemsDto);

        this.mockMvc.perform(get("/items")
                        .param("available", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseContent));
    }

    @Test
    public void whenGet_ParamAvailableIsNotExisted_thenReturnAllItems() throws Exception {
        Mockito.when(itemService.getAvailableItems()).thenReturn(availableItems);
        Mockito.when(itemService.getItems()).thenReturn(mockedItemsDto);

        final String expectedResponseContent = mapper.writeValueAsString(mockedItemsDto);

        this.mockMvc.perform(get("/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseContent));
    }

    @Test
    public void whenPost_thenResponseBodyIsEmptyAndStatusIsCreated() throws Exception {
        Mockito.doNothing().when(itemService).createItem(isA(ItemDTO.class));

        final String requestBody = mapper.writeValueAsString(mockedItemDTO);

        final String expectedResponseContent = "";

        this.mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedResponseContent));
    }

    @Test
    public void whenGet_Id_thenReturnCountOfItem() throws Exception {
        Mockito.doReturn(mockedItem.getAmount()).when(itemService).getCountOfItem(isA(UUID.class));

        final String expectedResponseContent = mockedItem.getAmount().toString();

        this.mockMvc.perform(get("/items/" + mockedItem.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseContent));
    }

    @Test
    public void whenPut_Id_thenResponseBodyIsEmptyAndStatusIsOk() throws Exception {
        Mockito.doNothing().when(itemService).updateItem(isA(UUID.class), isA(ItemDTO.class));

        final String expectedResponseContent = "";

        this.mockMvc.perform(delete("/items/" + mockedItem.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseContent));
    }

    @Test
    public void whenDelete_Id_thenResponseBodyIsEmptyAndStatusIsOk() throws Exception {
        Mockito.doNothing().when(itemService).deleteItem(isA(UUID.class));

        final String expectedResponseContent = "";

        this.mockMvc.perform(delete("/items/" + mockedItem.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseContent));
    }

}
