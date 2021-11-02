package com.itmo.microservices.shop.payment.impl.repository;

import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentLogRecordRepository extends JpaRepository<PaymentLogRecord, Integer> {

    List<PaymentLogRecord> findByUserIdAndOrderId(UUID userId, UUID orderId);

    List<PaymentLogRecord> findByUserId(UUID userId);

}
