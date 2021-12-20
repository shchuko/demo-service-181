package com.itmo.microservices.shop.payment.impl.repository;

import com.itmo.microservices.shop.payment.impl.entity.PaymentStatus;
import com.itmo.microservices.shop.payment.impl.entity.RefundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RefundOrderRepository extends JpaRepository<RefundOrder, UUID> {
    RefundOrder findByOrderId(UUID orderId);
    boolean existsByOrderId(UUID orderId);
}
