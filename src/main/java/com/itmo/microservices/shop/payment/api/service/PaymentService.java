package com.itmo.microservices.shop.payment.api.service;

import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentFailedException;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    PaymentSubmissionDto orderPayment(String orderId) throws PaymentFailedException;
}
