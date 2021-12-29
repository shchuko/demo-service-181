package com.itmo.microservices.shop.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.common.security.WithMockCustomUser;
import com.itmo.microservices.shop.common.test.NoWebSecurityTestCase;
import com.itmo.microservices.shop.order.HardcodedValues;
import com.itmo.microservices.shop.order.api.model.OrderDTO;
import com.itmo.microservices.shop.order.api.service.IOrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
class OrderControllerTest extends NoWebSecurityTestCase {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IOrderService service;

    private final static ObjectMapper mapper = new ObjectMapper();
    private final HardcodedValues values = new HardcodedValues();

    @Test
    @WithMockCustomUser
    void createOrder() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUuid(values.orderUUID);
        orderDTO.setTimeCreated(values.time);
        orderDTO.setStatus(values.collectedStatus.getName());
        orderDTO.setItemsMap(new HashMap<>());
        orderDTO.setDeliveryDuration(values.slot);
        orderDTO.setPaymentHistory(new ArrayList<>());
        Mockito.when(service.createOrder(UUID.fromString("224ec6ce-3fea-11ec-9356-0242ac130003"))).thenReturn(orderDTO);
        final String expectedResponseContent = mapper.writeValueAsString(orderDTO);

        mockMvc.perform(post("/orders"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedResponseContent));

        Mockito.verify(service).createOrder(UUID.fromString("224ec6ce-3fea-11ec-9356-0242ac130003"));
        Mockito.verifyNoMoreInteractions(service);
    }
}
