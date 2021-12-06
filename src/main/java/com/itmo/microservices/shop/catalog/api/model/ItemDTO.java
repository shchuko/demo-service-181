package com.itmo.microservices.shop.catalog.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.UUID;

public class ItemDTO {
    private UUID id;

    @JsonProperty("title")
    private String name;

    private String description;
    private int price;
    private int amount;

    public ItemDTO(UUID id, String name, String description, int price, int amount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.amount = amount;
    }

    public ItemDTO() {
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ItemDTO itemDTO = (ItemDTO) o;
        return Objects.equals(id, itemDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
