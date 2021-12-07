package com.itmo.microservices.shop.payment.impl.repository;

import com.itmo.microservices.shop.payment.impl.entity.PaymentTransactionsProcessorWriteback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface PaymentTransactionsProcessorWritebackRepository extends JpaRepository<PaymentTransactionsProcessorWriteback, UUID> {

}
