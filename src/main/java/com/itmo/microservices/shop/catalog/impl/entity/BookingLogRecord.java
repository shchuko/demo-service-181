package com.itmo.microservices.shop.catalog.impl.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@IdClass(BookingLogRecordId.class)
public class BookingLogRecord {

    public BookingLogRecord() { }

    public BookingLogRecord(UUID bookingId, UUID itemId, Integer amount, BookingStatus status) {
        this.bookingId = bookingId;
        this.itemId = itemId;
        this.amount = amount;
        this.status = status;
    }

    @Id
    private UUID bookingId;

    @Id
    private UUID itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    private BookingStatus status;

    private Integer amount;
    private Long timestamp;

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
