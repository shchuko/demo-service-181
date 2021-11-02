package com.itmo.microservices.shop.payment.impl.service;

import com.itmo.microservices.shop.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInfoNotFoundException;
import com.itmo.microservices.shop.payment.impl.repository.PaymentLogRecordRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FinLogService {

    private final PaymentLogRecordRepository paymentLogRecordRepo;

    public FinLogService(PaymentLogRecordRepository paymentLogRecordRepo) {
        this.paymentLogRecordRepo = paymentLogRecordRepo;
    }


    public List<UserAccountFinancialLogRecordDto> getUserFinanceLog(UUID user_id)
            throws PaymentInfoNotFoundException {

        var paymentLogRecords = paymentLogRecordRepo.findByUserId(user_id);
        return getUserAccountFinancialLogRecordDtos(paymentLogRecords);
    }

    public List<UserAccountFinancialLogRecordDto> getUserFinanceLog(UUID user_id, String orderId)
            throws PaymentInfoNotFoundException {

        var paymentLogRecords = paymentLogRecordRepo.findByUserIdAndOrderId(user_id,
                UUID.fromString(orderId));

        return getUserAccountFinancialLogRecordDtos(paymentLogRecords);
    }

    private List<UserAccountFinancialLogRecordDto> getUserAccountFinancialLogRecordDtos(List<PaymentLogRecord> paymentLogRecords)
            throws PaymentInfoNotFoundException {
        if (paymentLogRecords.isEmpty()) {
            throw new PaymentInfoNotFoundException("No payment information found.");
        }
        List<UserAccountFinancialLogRecordDto> userAccountFinancialLogRecordDtos = new ArrayList<>();
        for (PaymentLogRecord paymentLogRecord : paymentLogRecords) {
            userAccountFinancialLogRecordDtos.add(UserAccountFinancialLogRecordDto.toModel(paymentLogRecord));
        }
        return userAccountFinancialLogRecordDtos;
    }


}
