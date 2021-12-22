package com.itmo.microservices.shop.catalog.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmo.microservices.shop.catalog.impl.entity.BookingStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class BookingLogRecordDTO {

    public BookingLogRecordDTO(UUID bookingId, UUID itemId, Integer amount, BookingStatus status, Long timestamp) {
        this.bookingId = bookingId;
        this.itemId = itemId;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public BookingLogRecordDTO() { }

    @JsonProperty("bookingId")
    private UUID bookingId;

    @JsonProperty("itemId")
    private UUID itemId;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("status")
    private BookingStatus status;

    @JsonProperty("timestamp")
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
