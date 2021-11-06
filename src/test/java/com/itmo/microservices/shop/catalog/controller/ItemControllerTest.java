package com.itmo.microservices.shop.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.common.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import com.itmo.microservices.shop.common.test.NoWebSecurityTestCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class ItemControllerTest extends NoWebSecurityTestCase {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    private final static ObjectMapper mapper = new ObjectMapper();

    private final List<ItemDTO> availableItems = Collections.unmodifiableList(hardcodedValues.mockedItemsDto.stream()
            .filter(item -> item.getAmount() > 0).collect(Collectors.toList()));

    private final List<ItemDTO> notAvailableItems = Collections.unmodifiableList(hardcodedValues.mockedItemsDto.stream()
            .filter(item -> item.getAmount() <= 0).collect(Collectors.toList()));

    @Test
    public void whenGet_ParamAvailableTrue_thenReturnItemsWithCountMoreThanZero() throws Exception {
        Mockito.when(itemService.getAvailableItems()).thenReturn(availableItems);
        Mockito.when(itemService.getItems()).thenReturn(hardcodedValues.mockedItemsDto);

        final String expectedResponseContent = mapper.writeValueAsString(availableItems);

        this.mockMvc.perform(get("/items")
                        .param("available", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseContent));
        Mockito.verify(itemService).getAvailableItems();
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void whenGet_ParamAvailableFalse_thenReturnNotAvailableItems() throws Exception {
        Mockito.when(itemService.getUnavailableItems()).thenReturn(notAvailableItems);
        Mockito.when(itemService.getItems()).thenReturn(hardcodedValues.mockedItemsDto);

        final String expectedResponseContent = mapper.writeValueAsString(notAvailableItems);

        this.mockMvc.perform(get("/items")
                        .param("available", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseContent));
        Mockito.verify(itemService).getUnavailableItems();
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void whenGet_ParamAvailableIsNotExisted_thenReturnAllItems() throws Exception {
        Mockito.when(itemService.getAvailableItems()).thenReturn(availableItems);
        Mockito.when(itemService.getItems()).thenReturn(hardcodedValues.mockedItemsDto);

        final String expectedResponseContent = mapper.writeValueAsString(hardcodedValues.mockedItemsDto);

        this.mockMvc.perform(get("/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseContent));

        Mockito.verify(itemService).getItems();
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void whenPost_thenResponseBodyIsEmptyAndStatusIsCreated() throws Exception {
        Mockito.doNothing().when(itemService).createItem(isA(ItemDTO.class));

        final String requestBody = mapper.writeValueAsString(hardcodedValues.mockedItemDTO);

        final String expectedResponseContent = "";

        this.mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedResponseContent));

        Mockito.verify(itemService).createItem(Mockito.any(ItemDTO.class));
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void whenGet_Id_ItemExisted_thenReturnCountOfItem() throws Exception {
        Mockito.doReturn(hardcodedValues.mockedItem.getAmount()).when(itemService).getCountOfItem(isA(UUID.class));

        final String expectedResponseContent = hardcodedValues.mockedItem.getAmount().toString();

        this.mockMvc.perform(get("/items/" + hardcodedValues.mockedItem.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseContent));

        Mockito.verify(itemService).getCountOfItem(Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void whenGet_Id_ItemNotExisted_thenThrowItemNotFoundException() throws Exception {
        Mockito.when(itemService.getCountOfItem(hardcodedValues.mockedUUID))
                .thenThrow(ItemNotFoundException.class);

        this.mockMvc.perform(get("/items/" + hardcodedValues.mockedItem.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());

        Mockito.verify(itemService).getCountOfItem(Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void whenPut_Id_thenResponseBodyIsEmptyAndStatusIsOk() throws Exception {
        Mockito.doNothing().when(itemService).updateItem(isA(UUID.class), isA(ItemDTO.class));

        final String expectedResponseContent = "";

        this.mockMvc.perform(delete("/items/" + hardcodedValues.mockedItem.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseContent));

        Mockito.verify(itemService).deleteItem(Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void whenDelete_Id_thenResponseBodyIsEmptyAndStatusIsOk() throws Exception {
        Mockito.doNothing().when(itemService).deleteItem(isA(UUID.class));

        final String expectedResponseContent = "";

        this.mockMvc.perform(delete("/items/" + hardcodedValues.mockedItem.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseContent));

        Mockito.verify(itemService).deleteItem(Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(itemService);
    }

}
