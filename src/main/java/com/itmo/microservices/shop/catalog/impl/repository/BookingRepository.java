package com.itmo.microservices.shop.catalog.impl.repository;

import com.itmo.microservices.shop.catalog.impl.entity.Booking;
import com.itmo.microservices.shop.catalog.impl.entity.BookingLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

}
