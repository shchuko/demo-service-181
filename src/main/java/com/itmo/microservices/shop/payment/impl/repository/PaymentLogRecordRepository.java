package com.itmo.microservices.shop.payment.impl.repository;

import com.itmo.microservices.shop.payment.impl.entity.FinancialOperationType;
import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import com.itmo.microservices.shop.payment.impl.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaymentLogRecordRepository extends JpaRepository<PaymentLogRecord, Integer> {

    List<PaymentLogRecord> findByUserIdAndOrderId(UUID userId, UUID orderId);

    List<PaymentLogRecord> findByUserId(UUID userId);

    PaymentLogRecord findByTransactionId(UUID transactionId);

    boolean existsByOrderIdAndPaymentStatusAndFinancialOperationType(UUID orderId, PaymentStatus paymentStatus,
                                                                     FinancialOperationType financialOperationType);

}
