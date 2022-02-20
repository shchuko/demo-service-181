package com.itmo.microservices.shop.catalog.impl.repository;

import com.itmo.microservices.shop.catalog.impl.entity.BookingLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingLogRecordRepository extends JpaRepository<BookingLogRecord, UUID> {

}
