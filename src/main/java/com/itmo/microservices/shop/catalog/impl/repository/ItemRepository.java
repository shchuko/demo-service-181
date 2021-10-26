package com.itmo.microservices.shop.catalog.impl.repository;

import com.itmo.microservices.shop.catalog.impl.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    @Query(
            value = "select * from Item where Item.count > ?1",
            nativeQuery = true
    )
    List<Item> findAllWhereCountMoreThan(Integer value);
}
