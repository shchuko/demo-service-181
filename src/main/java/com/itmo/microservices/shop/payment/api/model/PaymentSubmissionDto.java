package com.itmo.microservices.shop.payment.api.model;

import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PaymentSubmissionDto {

    private Long timestamp;
    private UUID transactionId;

    public static PaymentSubmissionDto toModel(PaymentLogRecord paymentLogRecord) {
        PaymentSubmissionDto model = new PaymentSubmissionDto();
        model.setTimestamp(paymentLogRecord.getTimestamp());
        model.setTransactionId(paymentLogRecord.getTransactionId());
        model.setTimestamp(paymentLogRecord.getTimestamp());
        return model;
    }

}
