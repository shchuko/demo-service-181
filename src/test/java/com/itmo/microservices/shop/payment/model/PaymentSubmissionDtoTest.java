package com.itmo.microservices.shop.payment.model;

import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.common.HardcodedValues;
import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class PaymentSubmissionDtoTest {
    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Test
    public void whenToModelThenReturnUserAccountFinancialLogRecordDto() {
        int index = (new Random()).nextInt(hardcodedValues.paymentLogRecords.size());
        PaymentLogRecord paymentLogRecord = hardcodedValues.paymentLogRecords.get(index);

        PaymentSubmissionDto expected = new PaymentSubmissionDto();
        expected.setTimestamp(paymentLogRecord.getTimestamp());
        expected.setTransactionId(paymentLogRecord.getTransactionId());

        PaymentSubmissionDto actual = PaymentSubmissionDto.toModel(paymentLogRecord);
        Assertions.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        Assertions.assertEquals(expected.getTransactionId(), actual.getTransactionId());
    }
}
