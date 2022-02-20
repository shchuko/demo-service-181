package com.itmo.microservices.shop.catalog.api.model;

import java.util.Set;
import java.util.UUID;

public class BookingCreationDto {

    private UUID uuid;
    private Set<UUID> failedItems;

    public BookingCreationDto() {
    }

    public BookingCreationDto(UUID uuid, Set<UUID> failedItems) {
        this.uuid = uuid;
        this.failedItems = failedItems;
    }

}
