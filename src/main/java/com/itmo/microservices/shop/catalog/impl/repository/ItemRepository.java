package com.itmo.microservices.shop.catalog.impl.repository;

import com.itmo.microservices.shop.catalog.impl.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository<T> extends JpaRepository<Item, UUID> {

  @Query(value = "select it from Item it where it.count > 0")
  List<T> returnAvailableItems();

  @Query("select it.count from Item it where it.uuid = :uuid")
  Integer getCount(@Param("uuid") UUID uuid);
}
