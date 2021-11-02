package com.itmo.microservices.shop.order.impl.repository;

import com.itmo.microservices.shop.order.impl.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IOrderItemRepository extends JpaRepository<OrderItem, UUID> {

}
