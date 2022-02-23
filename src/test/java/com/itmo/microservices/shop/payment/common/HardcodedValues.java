package com.itmo.microservices.shop.payment.common;

import com.itmo.microservices.shop.payment.api.model.PaymentLogRecordDto;
import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.shop.payment.impl.entity.FinancialOperationType;
import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import com.itmo.microservices.shop.payment.impl.entity.PaymentStatus;
import com.itmo.microservices.shop.payment.impl.mapper.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HardcodedValues {

    public final List<FinancialOperationType> financialOperationTypes = Arrays.asList(new FinancialOperationType(1, "WITHDRAW"),
            new FinancialOperationType(2, "REFUND"));

    public final List<PaymentStatus> paymentStatuses = Arrays.asList(new PaymentStatus(1, "FAILED"),
            new PaymentStatus(2, "SUCCESS"));

    public final List<UUID> ids = Arrays.asList(UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
    );


    public final List<UUID> orderIds = Arrays.asList(UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
    );


    public final List<UUID> transactionIds = Arrays.asList(UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
    );


    public final List<UUID> userIds = Arrays.asList(UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.fromString("e99b7ee6-ee5e-4cb9-9cdd-33fe50765e6e")
    );


    public final List<PaymentLogRecord> paymentLogRecords = List.of(
            new PaymentLogRecord(ids.get(0), 100, 130L, orderIds.get(0), transactionIds.get(0), userIds.get(0),
                    paymentStatuses.get(0), financialOperationTypes.get(0)),
            new PaymentLogRecord(ids.get(1), 150, 190L, orderIds.get(1), transactionIds.get(1), userIds.get(1),
                    paymentStatuses.get(1), financialOperationTypes.get(0)),
            new PaymentLogRecord(ids.get(2), 90, 30L, orderIds.get(2), transactionIds.get(2), userIds.get(2),
                    paymentStatuses.get(0), financialOperationTypes.get(1)),
            new PaymentLogRecord(ids.get(3), 30, 115L, orderIds.get(3), transactionIds.get(3), userIds.get(5),
                    paymentStatuses.get(1), financialOperationTypes.get(1)),
            new PaymentLogRecord(ids.get(4), 900, 254L, orderIds.get(4), transactionIds.get(4), userIds.get(4),
                    paymentStatuses.get(0), financialOperationTypes.get(1)),
            new PaymentLogRecord(ids.get(2), 902, 30L, orderIds.get(2), transactionIds.get(2), userIds.get(2),
                    paymentStatuses.get(0), financialOperationTypes.get(1)),
            new PaymentLogRecord(ids.get(3), 304, 121L, orderIds.get(3), transactionIds.get(3), userIds.get(3),
                    paymentStatuses.get(0), financialOperationTypes.get(1)),
            new PaymentLogRecord(ids.get(4), 980, 151L, orderIds.get(4), transactionIds.get(4), userIds.get(5),
                    paymentStatuses.get(1), financialOperationTypes.get(1))
    );

    public final List<PaymentLogRecordDto> paymentLogRecordDtos = paymentLogRecords.stream()
            .map(Mappers::buildPaymentLogRecordDto).collect(Collectors.toList());

    public final List<PaymentSubmissionDto> paymentSubmissionDtos = paymentLogRecords.stream()
            .map(PaymentSubmissionDto::toModel).collect(Collectors.toList());

    public final List<UserAccountFinancialLogRecordDto> userAccountFinancialLogRecordDto = paymentLogRecords.stream()
            .map(Mappers::buildFinLogRecordDto).collect(Collectors.toList());

    public final PaymentSubmissionDto paymentSubmissionDto = new PaymentSubmissionDto(1922L,
            UUID.fromString("7d9689b4-9b8e-4ed4-bef7-7fe4d691a658"));
}
