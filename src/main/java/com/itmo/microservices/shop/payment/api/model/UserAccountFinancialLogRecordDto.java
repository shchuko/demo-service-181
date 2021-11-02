package com.itmo.microservices.shop.payment.api.model;

import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class UserAccountFinancialLogRecordDto {
    private String FinancialOperationType;
    private Integer amount;
    private UUID orderId;
    private UUID paymentTransactionId;
    private Long timestamp;

    public static UserAccountFinancialLogRecordDto toModel(PaymentLogRecord paymentLogRecord) {
        UserAccountFinancialLogRecordDto model = new UserAccountFinancialLogRecordDto();

        model.setFinancialOperationType(paymentLogRecord.getFinancialOperationType().getName());
        model.setAmount(paymentLogRecord.getAmount());
        model.setOrderId(paymentLogRecord.getOrderId());
        model.setPaymentTransactionId(paymentLogRecord.getTransactionId());
        model.setTimestamp(paymentLogRecord.getTimestamp());

        return model;
    }

}
