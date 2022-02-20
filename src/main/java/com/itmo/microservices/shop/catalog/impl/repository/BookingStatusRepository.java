package com.itmo.microservices.shop.catalog.impl.repository;

import com.itmo.microservices.shop.catalog.impl.entity.BookingStatus;
import com.itmo.microservices.shop.payment.impl.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingStatusRepository extends JpaRepository<BookingStatus, Integer> {
    BookingStatus getBookingStatusByName(String name);
}
