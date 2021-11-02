package com.itmo.microservices.shop.payment.impl.service;


import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.api.service.PaymentService;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentFailedException;
import com.itmo.microservices.shop.payment.impl.repository.FinancialOperationTypeRepository;
import com.itmo.microservices.shop.payment.impl.repository.PaymentLogRecordRepository;
import com.itmo.microservices.shop.payment.impl.repository.PaymentStatusRepository;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class DefaultPaymentService implements PaymentService {

    private final PaymentLogRecordRepository paymentLogRecordRepo;
    private final FinancialOperationTypeRepository financialOperationTypeRepository;
    private final PaymentStatusRepository paymentStatusRepository;

    public DefaultPaymentService(PaymentLogRecordRepository paymentLogRecordRepo,
                                 FinancialOperationTypeRepository financialOperationTypeRepository,
                                 PaymentStatusRepository paymentStatusRepository) {
        this.paymentLogRecordRepo = paymentLogRecordRepo;
        this.financialOperationTypeRepository = financialOperationTypeRepository;
        this.paymentStatusRepository = paymentStatusRepository;
    }

    public PaymentSubmissionDto orderPayment(String orderId) throws PaymentFailedException {

        boolean isSuccess = (new Random()).nextBoolean();
        if (isSuccess) {
            PaymentSubmissionDto paymentSubmissionDto = new PaymentSubmissionDto();
            paymentSubmissionDto.setTimestamp(1922L);
            paymentSubmissionDto.setTransactionId(UUID.fromString("7d9689b4-9b8e-4ed4-bef7-7fe4d691a658"));
            return paymentSubmissionDto;
        } else {
            throw new PaymentFailedException("Payment failed.");
        }
    }

}
