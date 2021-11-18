package com.itmo.microservices.shop.user.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateAdminDto {
    @JsonProperty("secret")
    private String adminSecret;

    private boolean isAdmin;

    public UpdateAdminDto(String adminSecret, boolean isAdmin) {
        this.adminSecret = adminSecret;
        this.isAdmin = isAdmin;
    }

    public String getAdminSecret() {
        return adminSecret;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setAdminSecret(String adminSecret) {
        this.adminSecret = adminSecret;
    }
}
