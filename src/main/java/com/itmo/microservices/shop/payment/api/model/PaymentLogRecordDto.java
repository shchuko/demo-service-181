package com.itmo.microservices.shop.payment.api.model;

import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class PaymentLogRecordDto {

    private Long timestamp;
    private String paymentStatus;
    private Integer amount;
    private UUID transactionId;

    public static PaymentLogRecordDto toModel(PaymentLogRecord paymentLogRecord) {
        PaymentLogRecordDto model = new PaymentLogRecordDto();
        model.setTimestamp(paymentLogRecord.getTimestamp());
        model.setPaymentStatus(paymentLogRecord.getPaymentStatus().getName());
        model.setTransactionId(paymentLogRecord.getTransactionId());
        model.setAmount(paymentLogRecord.getAmount());
        return model;
    }
}
