package com.itmo.microservices.shop.order.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class BookingDTO {

    public BookingDTO() {
    }

    public BookingDTO(UUID uuid, Set<UUID> failedItems) {
        this.uuid = uuid;
        this.failedItems = failedItems;
    }

    @JsonProperty("id")
    private UUID uuid;
    @JsonProperty("failedItems")
    private Set<UUID> failedItems;
}
