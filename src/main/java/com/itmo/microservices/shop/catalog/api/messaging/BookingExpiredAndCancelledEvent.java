package com.itmo.microservices.shop.catalog.api.messaging;

import java.util.UUID;

public class BookingExpiredAndCancelledEvent extends BookingEvent {

    public BookingExpiredAndCancelledEvent(UUID bookingId) {
        super(bookingId);
    }
}
