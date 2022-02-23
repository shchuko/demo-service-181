package com.itmo.microservices.shop.payment.model;

import com.itmo.microservices.shop.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.shop.payment.common.HardcodedValues;
import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class UserAccountFinancialLogRecordDtoTest {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Disabled
    @Test
    public void whenToModelThenReturnUserAccountFinancialLogRecordDto() {
        int index = (new Random()).nextInt(hardcodedValues.paymentLogRecords.size());
        PaymentLogRecord paymentLogRecord = hardcodedValues.paymentLogRecords.get(index);
        /*
        TODO implement me
         */
//        UserAccountFinancialLogRecordDto expected = new UserAccountFinancialLogRecordDto();
//        expected.setFinancialOperationType(paymentLogRecord.getFinancialOperationType().getName());
//        expected.setAmount(paymentLogRecord.getAmount());
//        expected.setOrderId(paymentLogRecord.getOrderId());
//        expected.setPaymentTransactionId(paymentLogRecord.getTransactionId());
//        expected.setTimestamp(paymentLogRecord.getTimestamp());
//
//
//        UserAccountFinancialLogRecordDto actual = UserAccountFinancialLogRecordDto.toModel(paymentLogRecord);
//        Assertions.assertEquals(expected.getFinancialOperationType(), actual.getFinancialOperationType());
//        Assertions.assertEquals(expected.getAmount(), actual.getAmount());
//        Assertions.assertEquals(expected.getOrderId(), actual.getOrderId());
//        Assertions.assertEquals(expected.getPaymentTransactionId(), actual.getPaymentTransactionId());
//        Assertions.assertEquals(expected.getTimestamp(), actual.getTimestamp());
    }
}
