package com.itmo.microservices.shop.order.impl.repository;

import com.itmo.microservices.shop.order.impl.entity.OrderTable;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrderTableRepository extends JpaRepository<OrderTable, UUID> {
    @NotNull
    @Override
    Optional<OrderTable> findById(@NotNull UUID uuid);
}

