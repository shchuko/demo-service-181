package com.itmo.microservices.shop.delivery.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.common.test.NoWebSecurityTestCase;
import com.itmo.microservices.shop.delivery.api.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class DeliveryControllerTest extends NoWebSecurityTestCase {
    @MockBean
    private DeliveryService deliveryService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getTimeSlots_canGetUnlimited() throws Exception {
        int slotsTotal = 10;
        var mockedSlots = Stream.iterate(1, n -> n + 1).limit(slotsTotal).collect(Collectors.toList());

        Mockito.doReturn(mockedSlots).when(deliveryService).getDeliverySlots();

        var expected = mapper.writeValueAsString(mockedSlots);
        mockMvc.perform(get("/delivery/slots"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expected));
    }

    @Test
    void getTimeSlots_canGetLimited() throws Exception {
        int slotsTotal = 10;
        int limit = 5;

        var mockedSlots = Stream.iterate(1, n -> n + 1).limit(slotsTotal).collect(Collectors.toList());
        var mockedSlotsLimited = mockedSlots.stream().limit(limit).collect(Collectors.toList());

        Mockito.doReturn(mockedSlots).when(deliveryService).getDeliverySlots();
        Mockito.doReturn(mockedSlotsLimited).when(deliveryService).getDeliverySlots(limit);

        var expected = mapper.writeValueAsString(mockedSlotsLimited);
        mockMvc.perform(get("/delivery/slots").param("number", limit + ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    void getTimeSlots_canGetLesserThanLimit() throws Exception {
        int slotsTotal = 5;
        int limit = 10;

        var mockedSlots = Stream.iterate(1, n -> n + 1).limit(slotsTotal).collect(Collectors.toList());
        var mockedSlotsLimited = mockedSlots.stream().limit(limit).collect(Collectors.toList());

        Mockito.doReturn(mockedSlots).when(deliveryService).getDeliverySlots();
        Mockito.doReturn(mockedSlotsLimited).when(deliveryService).getDeliverySlots(limit);

        var expected = mapper.writeValueAsString(mockedSlotsLimited);
        mockMvc.perform(get("/delivery/slots").param("number", limit + ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expected));
    }
}
