package com.itmo.microservices.shop.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.common.test.NoWebSecurityTestCase;
import com.itmo.microservices.shop.payment.api.controller.PaymentController;
import com.itmo.microservices.shop.payment.common.HardcodedValues;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentFailedException;
import com.itmo.microservices.shop.payment.impl.service.DefaultPaymentService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class PaymentControllerTest extends NoWebSecurityTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentController paymentController;

    @MockBean
    private DefaultPaymentService service;

    private final HardcodedValues hardcodedValues = new HardcodedValues();
    private final static ObjectMapper mapper = new ObjectMapper();

    @Disabled
    @Test
    public void whenPaymentIsSuccessThenReturnPaymentSubmissionDto() throws Exception {
        Mockito.when(service.payForOrder(Mockito.any(), Mockito.any()))
                .thenReturn(hardcodedValues.paymentSubmissionDto);

        mockMvc.perform(post("/orders/{orderId}/payment", "7d9689b4-9b8e-4ed4-bef7-7fe4d691a658"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(hardcodedValues.paymentSubmissionDto)));

        Mockito.verify(service).payForOrder(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(service);
    }

    @Disabled
    @Test
    public void whenPaymentIsFailedThenPaymentFailedException() throws Exception {
        Mockito.when(service.payForOrder(Mockito.any(), Mockito.any()))
                .thenThrow(new PaymentFailedException("Payment failed."));

        mockMvc.perform(post("/orders/{orderId}/payment", "7d9689b4-9b8e-4ed4-bef7-7fe4d691a658"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Payment failed."));

        Mockito.verify(service).payForOrder(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void dummyTest() {
        // Required to reduce "failed to load application context" error
        // TODO remove when at least one other test is enabled
    }
}
