package com.itmo.microservices.shop.user.api.model;

import lombok.Data;

import java.util.UUID;

@Data
public class AdminDTO {
    private UUID id;
    private boolean isAdmin;

    public AdminDTO(UUID id, boolean isAdmin) {
        this.id = id;
        this.isAdmin = isAdmin;
    }
}
