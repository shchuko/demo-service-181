package com.itmo.microservices.shop.catalog.controller;

import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.common.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.service.ItemService;
import com.itmo.microservices.shop.common.test.DefaultSecurityTestCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    private final List<ItemDTO> availableItems = Collections.unmodifiableList(hardcodedValues.mockedItemsDto.stream()
            .filter(item -> item.getAmount() > 0).collect(Collectors.toList()));


    @Test
    public void whenGetWithoutAuthentication_thenReturnForbiddenStatus() throws Exception {
        Mockito.when(itemService.getAvailableItems()).thenReturn(availableItems);
        Mockito.when(itemService.getItems()).thenReturn(hardcodedValues.mockedItemsDto);

        this.mockMvc.perform(get("/items")
                        .param("available", "true"))
                .andDo(print())
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(itemService);
    }
}
