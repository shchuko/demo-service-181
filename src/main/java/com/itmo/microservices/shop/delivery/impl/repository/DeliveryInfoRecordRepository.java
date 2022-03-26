package com.itmo.microservices.shop.delivery.impl.repository;

import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoRecordDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryInfoRecordRepository extends
        JpaRepository<DeliveryInfoRecordDto, UUID> {

    List<DeliveryInfoRecordDto> findAllByOrderId(UUID orderId);
}
