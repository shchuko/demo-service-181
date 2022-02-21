package com.itmo.microservices.shop.catalog.api.model;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class BookingDescriptionDto {
    private UUID bookingId;
    private Map<UUID, Integer> successItems;
    private Map<UUID, Integer> failedItems;

    @NotNull
    public UUID getBookingId() {
        return bookingId;
    }


    public BookingDescriptionDto() {
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public Map<UUID, Integer> getSuccessItems() {
        return successItems;
    }

    public void setSuccessItems(Map<UUID, Integer> successItems) {
        this.successItems = successItems;
    }

    public Map<UUID, Integer> getFailedItems() {
        return failedItems;
    }

    public void setFailedItems(Map<UUID, Integer> failedItems) {
        this.failedItems = failedItems;
    }

    public BookingDescriptionDto(Map<UUID, Integer> successItems, Map<UUID, Integer> failedItems) {
        this.successItems = successItems;
        this.failedItems = failedItems;
    }

    public BookingDescriptionDto(UUID bookingId, Map<UUID, Integer> successItems, Map<UUID, Integer> failedItems) {
        this.bookingId = bookingId;
        this.successItems = successItems;
        this.failedItems = failedItems;
    }
}
