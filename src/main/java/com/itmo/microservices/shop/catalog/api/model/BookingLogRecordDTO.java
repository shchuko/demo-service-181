package com.itmo.microservices.shop.catalog.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class BookingLogRecordDTO {

    public BookingLogRecordDTO(UUID bookingId, UUID itemId, Integer amount, String bookingStatus, Long timestamp) {
        this.bookingId = bookingId;
        this.itemId = itemId;
        this.amount = amount;
        this.status = bookingStatus;
        this.timestamp = timestamp;
    }

    public BookingLogRecordDTO() {
    }

    @JsonProperty("bookingId")
    private UUID bookingId;

    @JsonProperty("itemId")
    private UUID itemId;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("status")
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
