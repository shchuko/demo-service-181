package com.itmo.microservices.shop.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.common.test.NoWebSecurityTestCase;
import com.itmo.microservices.shop.payment.api.controller.FinLogController;
import com.itmo.microservices.shop.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.shop.payment.api.service.PaymentService;
import com.itmo.microservices.shop.payment.common.HardcodedValues;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInfoNotFoundException;
import com.itmo.microservices.shop.payment.impl.mapper.Mappers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class FinLogControllerTest extends NoWebSecurityTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FinLogController controller;

    @MockBean
    PaymentService service;

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    private final static ObjectMapper mapper = new ObjectMapper();

    @Disabled
    @Test
    public void whenGetUserFinancialLogWithOrderIdReturnListOfUserAccountFinancialLogRecordDto() throws Exception {
        UUID userId = hardcodedValues.userIds.get(5);
        UUID orderId = hardcodedValues.paymentLogRecords.stream()
                .filter(i -> i.getUserId() == userId).findFirst().get().getOrderId();

        var expected = hardcodedValues.paymentLogRecords.stream()
                .filter(i -> i.getUserId() == userId && i.getOrderId() == orderId)
                .map(Mappers::buildFinLogRecordDto).collect(Collectors.toList());

        Mockito.when(service.listUserFinLog(userId, orderId))
                .thenReturn(expected);

        this.mockMvc.perform(get("/finlog").param("orderId", orderId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expected)));

        Mockito.verify(service).listUserFinLog(Mockito.any(UUID.class), Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(service);
    }

    @Disabled
    @Test
    public void whenGetUserFinancialLogWithoutOrderIdReturnListOfUserAccountFinancialLogRecordDto() throws Exception {
        UUID userId = hardcodedValues.userIds.get(5);

        var expected = hardcodedValues.paymentLogRecords.stream()
                .filter(i -> i.getUserId() == userId)
                .map(Mappers::buildFinLogRecordDto).collect(Collectors.toList());

        Mockito.when(service.listUserFinLog(userId))
                .thenReturn(expected);

        this.mockMvc.perform(get("/finlog"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expected)));

        Mockito.verify(service).listUserFinLog(Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(service);
    }

    @Disabled
    @Test
    public void whenGetUserFinancialLogWithOrderIdReturnPaymentInfoNotFoundException() throws Exception {
        UUID orderId = UUID.randomUUID();


        Mockito.when(service.listUserFinLog(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenThrow(new PaymentInfoNotFoundException("No payment information found."));

        this.mockMvc.perform(get("/finlog").param("orderId", orderId.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No payment information found."));

        Mockito.verify(service).listUserFinLog(Mockito.any(UUID.class), Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(service);
    }

    @Disabled
    @Test
    public void whenGetUserFinancialLogWithoutOrderIdReturnPaymentInfoNotFoundException() throws Exception {

        Mockito.when(service.listUserFinLog(Mockito.any(UUID.class)))
                .thenThrow(new PaymentInfoNotFoundException("No payment information found."));

        this.mockMvc.perform(get("/finlog"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No payment information found."));

        Mockito.verify(service).listUserFinLog(Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void dummyTest() {
        // Required to reduce "failed to load application context" error
        // TODO remove when at least one other test is enabled
    }
}
