package com.itmo.microservices.shop.catalog.api.messaging;

import java.util.UUID;

public abstract class BookingEvent {
    private UUID bookingId;

    public BookingEvent(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }
}
