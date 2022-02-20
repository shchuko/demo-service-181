package com.itmo.microservices.shop.catalog.impl.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@IdClass(BookingLogRecordId.class)
public class BookingLogRecord {

    @Id
    private UUID bookingId;

    @Id
    private UUID itemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bookingId",
            nullable = false,
            insertable = false,
            updatable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    private BookingLogRecordStatus bookingLogRecordStatus;

    private Integer amount;

    private Long timestamp;

    public BookingLogRecord() {

    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public BookingLogRecordStatus getBookingLogRecordStatus() {
        return bookingLogRecordStatus;
    }

    public void setBookingLogRecordStatus(BookingLogRecordStatus bookingLogRecordStatus) {
        this.bookingLogRecordStatus = bookingLogRecordStatus;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public BookingLogRecord(UUID bookingId, UUID itemId, BookingLogRecordStatus bookingLogRecordStatus, Integer amount, Long timestamp) {
        this.bookingId = bookingId;
        this.itemId = itemId;
        this.bookingLogRecordStatus = bookingLogRecordStatus;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
