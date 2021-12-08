package com.itmo.microservices.shop.delivery.impl.repository;

import com.itmo.microservices.shop.delivery.impl.entity.DeliveryTransactionsProcessorWriteback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryTransactionsProcessorWritebackRepository extends
        JpaRepository<DeliveryTransactionsProcessorWriteback, UUID> {
}
