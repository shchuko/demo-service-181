package com.itmo.microservices.shop.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.common.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import com.itmo.microservices.shop.common.security.WithMockCustomUser;
import com.itmo.microservices.shop.common.test.DefaultSecurityTestCase;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO: when role authorization will be done
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class ItemControllerRolesTest extends DefaultSecurityTestCase {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    private final static ObjectMapper mapper = new ObjectMapper();

    private final List<ItemDTO> availableItems = Collections.unmodifiableList(hardcodedValues.mockedItemsDto.stream()
            .filter(item -> item.getAmount() > 0).collect(Collectors.toList()));


    @Test
    public void whenGetItemsWithoutAuthentication_thenReturnForbiddenStatus() throws Exception {
        Mockito.when(itemService.getAvailableItems()).thenReturn(availableItems);
        Mockito.when(itemService.getItems()).thenReturn(hardcodedValues.mockedItemsDto);

        this.mockMvc.perform(get("/items"))
                .andDo(print())
                .andExpect(status().isForbidden());

        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    @WithMockCustomUser
    public void whenGetItemsWithAuthentication_thenReturnOkStatus() throws Exception {
        Mockito.when(itemService.getAvailableItems()).thenReturn(availableItems);
        Mockito.when(itemService.getItems()).thenReturn(hardcodedValues.mockedItemsDto);

        this.mockMvc.perform(get("/items"))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(itemService).getItems();
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    @WithMockCustomUser(roles={"ACCESS","ADMIN"})
    public void whenGetItemsWithAdminAuthentication_thenReturnOkStatus() throws Exception {
        Mockito.when(itemService.getAvailableItems()).thenReturn(availableItems);
        Mockito.when(itemService.getItems()).thenReturn(hardcodedValues.mockedItemsDto);

        this.mockMvc.perform(get("/items"))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(itemService).getItems();
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    @WithMockCustomUser(roles={"ACCESS","ADMIN"})
    public void whenUpdateItemWithAdminAuthentication_thenReturnOkStatus() throws Exception {
        Mockito.doNothing().when(itemService).updateItem(isA(UUID.class), isA(ItemDTO.class));

        final String requestBody = mapper.writeValueAsString(hardcodedValues.mockedItemDTO);
        final String expectedResponseContent = "";

        this.mockMvc.perform(put("/items/{id}", hardcodedValues.mockedItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseContent));

        Mockito.verify(itemService).updateItem(Mockito.any(UUID.class), Mockito.any(ItemDTO.class));
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    @WithMockCustomUser
    public void whenUpdateItemWithUserAuthentication_thenReturnForbiddenStatus() throws Exception {
        Mockito.doNothing().when(itemService).updateItem(isA(UUID.class), isA(ItemDTO.class));

        final String requestBody = mapper.writeValueAsString(hardcodedValues.mockedItemDTO);
        final String expectedResponseContent = "";

        this.mockMvc.perform(put("/items/{id}", hardcodedValues.mockedItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().string(expectedResponseContent));
    }
}
