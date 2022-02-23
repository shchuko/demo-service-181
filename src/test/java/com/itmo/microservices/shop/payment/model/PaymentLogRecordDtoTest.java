package com.itmo.microservices.shop.payment.model;

import com.itmo.microservices.shop.payment.api.model.PaymentLogRecordDto;
import com.itmo.microservices.shop.payment.common.HardcodedValues;
import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class PaymentLogRecordDtoTest {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Disabled
    @Test
    public void whenToModelThenReturnPaymentLogRecordDto() {
        int index = (new Random()).nextInt(hardcodedValues.paymentLogRecords.size());
        PaymentLogRecord paymentLogRecord = hardcodedValues.paymentLogRecords.get(index);

        /*
        TODO implement me
         */
//        PaymentLogRecordDto expected = new PaymentLogRecordDto();
//        expected.setTimestamp(paymentLogRecord.getTimestamp());
//        expected.setPaymentStatus(paymentLogRecord.getPaymentStatus().getName());
//        expected.setAmount(paymentLogRecord.getAmount());
//        expected.setTransactionId(paymentLogRecord.getTransactionId());
//
//        PaymentLogRecordDto actual = PaymentLogRecordDto.toModel(paymentLogRecord);
//        Assertions.assertEquals(expected.getPaymentStatus(), actual.getPaymentStatus());
//        Assertions.assertEquals(expected.getTimestamp(), actual.getTimestamp());
//        Assertions.assertEquals(expected.getAmount(), actual.getAmount());
//        Assertions.assertEquals(expected.getTransactionId(), actual.getTransactionId());
    }


}
