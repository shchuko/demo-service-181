package com.itmo.microservices.shop.order.impl.repository;

import com.itmo.microservices.shop.order.impl.entity.OrderStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrderStatusRepository extends JpaRepository<OrderStatus, UUID> {
    @NotNull
    Optional<OrderStatus> findOrderStatusByName(@NotNull String name);
}
