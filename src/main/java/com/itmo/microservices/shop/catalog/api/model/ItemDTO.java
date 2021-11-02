package com.itmo.microservices.shop.catalog.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private UUID id;

    @JsonProperty("title")
    private String name;

    private String description;
    private int price;
    private int amount;

}
