package com.itmo.microservices.shop.payment.service;

import com.itmo.microservices.shop.common.test.DefaultSecurityTestCase;
import com.itmo.microservices.shop.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.shop.payment.common.HardcodedValues;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInfoNotFoundException;
import com.itmo.microservices.shop.payment.impl.repository.PaymentLogRecordRepository;
import com.itmo.microservices.shop.payment.impl.service.FinLogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class FinLogServiceTest extends DefaultSecurityTestCase {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @MockBean
    private PaymentLogRecordRepository repository;

    @Autowired
    private FinLogService service;

    @Test
    public void whenPaymentLogRecordExistByUserIdThenListUserAccountFinancialLogRecordDtoReturn() {
        int index = (new Random()).nextInt(hardcodedValues.userIds.size());
        UUID userId = hardcodedValues.userIds.get(index);

        var correct = hardcodedValues.paymentLogRecords
                .stream().filter(i -> i.getUserId() == userId).collect(Collectors.toList());

        Mockito.when(repository.findByUserId(Mockito.any(UUID.class))).thenReturn(correct);

        List<UserAccountFinancialLogRecordDto> correctDTO = correct.stream()
                .map(UserAccountFinancialLogRecordDto::toModel).collect(Collectors.toList());
        try {
            var test = service.getUserFinanceLog(userId);
            Assertions.assertEquals(correctDTO, test);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        Mockito.verify(repository).findByUserId(Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(repository);
    }


    @Test
    public void whenPaymentLogRecordByUserIdNotExistThenPaymentInfoNotFoundException() {

        UUID userId = UUID.randomUUID();

        Mockito.when(repository.findByUserId(Mockito.any(UUID.class))).thenReturn(List.of());

        Assertions.assertThrows(PaymentInfoNotFoundException.class, () -> service.getUserFinanceLog(userId));
    }

    @Test
    public void whenPaymentLogRecordExistByUserIdAndOrderIdThenListUserAccountFinancialLogRecordDtoReturn() {
        int index = (new Random()).nextInt(hardcodedValues.userIds.size());
        UUID userId = hardcodedValues.userIds.get(index);
        UUID orderId = hardcodedValues.paymentLogRecords.stream()
                .filter(i -> i.getUserId() == userId).findFirst().get().getOrderId();

        var correct = hardcodedValues.paymentLogRecords
                .stream().filter(i -> i.getUserId() == userId && i.getOrderId() == orderId)
                .collect(Collectors.toList());

        Mockito.when(repository.findByUserIdAndOrderId(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenReturn(correct);

        List<UserAccountFinancialLogRecordDto> correctDTO = correct.stream()
                .map(UserAccountFinancialLogRecordDto::toModel).collect(Collectors.toList());
        try {
            var test = service.getUserFinanceLog(userId, orderId.toString());
            Assertions.assertEquals(correctDTO, test);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        Mockito.verify(repository).findByUserIdAndOrderId(Mockito.any(UUID.class), Mockito.any(UUID.class));
        Mockito.verifyNoMoreInteractions(repository);

    }

    @Test
    public void whenPaymentLogRecordByUserIdAndOrderIdNotExistThenPaymentInfoNotFoundException() {

        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Mockito.when(repository.findByUserIdAndOrderId(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenReturn(List.of());

        Assertions.assertThrows(PaymentInfoNotFoundException.class, () -> service.getUserFinanceLog(userId, orderId.toString()));
    }

}
