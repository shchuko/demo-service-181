package com.itmo.microservices.shop.catalog.impl.entity;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
public class Booking {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    private BookingStatus bookingStatus;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "bookingId")
    private Set<BookingLogRecord> bookingLogRecords;

    public Booking() {

    }

    public Booking(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Booking(BookingStatus bookingStatus, Set<BookingLogRecord> bookingLogRecords) {
        this.bookingStatus = bookingStatus;
        this.bookingLogRecords = bookingLogRecords;
    }


    public Booking(UUID id, BookingStatus bookingStatus, Set<BookingLogRecord> bookingLogRecords) {
        this.id = id;
        this.bookingStatus = bookingStatus;
        this.bookingLogRecords = bookingLogRecords;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
