package com.itmo.microservices.shop.delivery.impl.repository;

import com.itmo.microservices.shop.delivery.impl.entity.DeliveryInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryInfoRepository extends JpaRepository<DeliveryInfo, Long> {

    Optional<DeliveryInfo> findDeliveryInfoByOrderId(@NotNull UUID orderId);

    default List<Integer> getDeliveryTimeSlots() {
        return Arrays.asList(1, 235, 32, 456, 2);
    }
}
