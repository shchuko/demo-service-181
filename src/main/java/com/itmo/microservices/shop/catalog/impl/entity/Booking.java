package com.itmo.microservices.shop.catalog.impl.entity;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
public class Booking {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    private BookingStatus bookingStatus;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookingId")
    private Set<BookingLogRecord> bookingLogRecords;

    public Booking() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Booking(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Set<BookingLogRecord> getBookingLogRecords() {
        return bookingLogRecords;
    }

    public void setBookingLogRecords(Set<BookingLogRecord> bookingLogRecords) {
        this.bookingLogRecords = bookingLogRecords;
    }
}
