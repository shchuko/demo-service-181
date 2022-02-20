package com.itmo.microservices.shop.catalog.impl.repository;

import com.itmo.microservices.shop.catalog.impl.entity.BookingLogRecordStatus;
import com.itmo.microservices.shop.catalog.impl.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingLogRecordStatusRepository extends JpaRepository<BookingLogRecordStatus, Integer> {
    BookingLogRecordStatus getBookingLogRecordStatusByName(String name);
}
