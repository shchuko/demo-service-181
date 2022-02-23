package com.itmo.microservices.shop.payment.impl.mapper;

import com.itmo.microservices.shop.payment.api.model.PaymentLogRecordDto;
import com.itmo.microservices.shop.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;

public final class Mappers {
    public static PaymentLogRecordDto buildPaymentLogRecordDto(PaymentLogRecord logRecord) {
        return new PaymentLogRecordDto(logRecord.getTimestamp(),
                logRecord.getPaymentStatus().getName(),
                logRecord.getAmount(),
                logRecord.getTransactionId());
    }

    public static UserAccountFinancialLogRecordDto buildFinLogRecordDto(PaymentLogRecord logRecord) {
        return new UserAccountFinancialLogRecordDto(
                logRecord.getFinancialOperationType().getName(),
                logRecord.getAmount(),
                logRecord.getOrderId(),
                logRecord.getTransactionId(),
                logRecord.getTimestamp());
    }

    private Mappers() {
    }
}
