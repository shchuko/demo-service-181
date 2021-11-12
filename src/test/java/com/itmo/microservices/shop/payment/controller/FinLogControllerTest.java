package com.itmo.microservices.shop.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.common.security.WithMockCustomUser;
import com.itmo.microservices.shop.common.test.DefaultSecurityTestCase;
import com.itmo.microservices.shop.payment.api.controller.FinLogController;
import com.itmo.microservices.shop.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.shop.payment.common.HardcodedValues;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInfoNotFoundException;
import com.itmo.microservices.shop.payment.impl.service.FinLogService;
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
public class FinLogControllerTest extends DefaultSecurityTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FinLogController controller;

    @MockBean
    FinLogService service;

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    private final static ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockCustomUser(uuid="e99b7ee6-ee5e-4cb9-9cdd-33fe50765e6e")
    public void whenGetUserFinancialLogWithOrderIdReturnListOfUserAccountFinancialLogRecordDto() throws Exception {
        UUID userId = hardcodedValues.userIds.get(5);
        UUID orderId = hardcodedValues.paymentLogRecords.stream()
                .filter(i -> i.getUserId() == userId).findFirst().get().getOrderId();

        var expected = hardcodedValues.paymentLogRecords.stream()
                .filter(i -> i.getUserId() == userId && i.getOrderId() == orderId)
                .map(UserAccountFinancialLogRecordDto::toModel).collect(Collectors.toList());

        Mockito.when(service.getUserFinanceLog(userId, orderId.toString()))
                .thenReturn(expected);

        this.mockMvc.perform(get("/finlog").param("orderId", orderId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expected)));

        Mockito.verify(service).getUserFinanceLog(Mockito.any(UUID.class), Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(service);
    }


    @Test
    @WithMockCustomUser(uuid="e99b7ee6-ee5e-4cb9-9cdd-33fe50765e6e")
    public void whenGetUserFinancialLogWithoutOrderIdReturnListOfUserAccountFinancialLogRecordDto() throws Exception {
        UUID userId = hardcodedValues.userIds.get(5);

        var expected = hardcodedValues.paymentLogRecords.stream()
                .filter(i -> i.getUserId() == userId)
                .map(UserAccountFinancialLogRecordDto::toModel).collect(Collectors.toList());

        Mockito.when(service.getUserFinanceLog(userId))
                .thenReturn(expected);

        this.mockMvc.perform(get("/finlog"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expected)));

        Mockito.verify(service).getUserFinanceLog(Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(service);
    }


    @Test
    @WithMockCustomUser(uuid="e99b7ee6-ee5e-4cb9-9cdd-33fe50765e6e")
    public void whenGetUserFinancialLogWithOrderIdReturnPaymentInfoNotFoundException() throws Exception {
        UUID orderId = UUID.randomUUID();


        Mockito.when(service.getUserFinanceLog(Mockito.any(UUID.class), Mockito.any(String.class)))
                .thenThrow(new PaymentInfoNotFoundException("No payment information found."));

        this.mockMvc.perform(get("/finlog").param("orderId", orderId.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No payment information found."));

        Mockito.verify(service).getUserFinanceLog(Mockito.any(UUID.class), Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    @WithMockCustomUser(uuid="e99b7ee6-ee5e-4cb9-9cdd-33fe50765e6e")
    public void whenGetUserFinancialLogWithoutOrderIdReturnPaymentInfoNotFoundException() throws Exception {

        Mockito.when(service.getUserFinanceLog(Mockito.any(UUID.class)))
                .thenThrow(new PaymentInfoNotFoundException("No payment information found."));

        this.mockMvc.perform(get("/finlog"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No payment information found."));

        Mockito.verify(service).getUserFinanceLog(Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(service);
    }
}
