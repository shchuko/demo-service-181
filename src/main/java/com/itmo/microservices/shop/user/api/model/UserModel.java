package com.itmo.microservices.shop.user.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itmo.microservices.shop.user.impl.userdetails.UserAuth;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.UUID;

public class UserModel {
    private UUID uuid;
    private String username;
    private Boolean isAdmin;

    @JsonIgnore
    private String password;

    public UserModel(UUID uuid, String username, String password, Boolean isAdmin) {
        this.uuid = uuid;
        this.username = username;
        this.isAdmin = isAdmin;
        this.password = password;
    }

    public UserAuth userDetails() {
        SimpleGrantedAuthority role = isAdmin ? new SimpleGrantedAuthority("ADMIN") : new SimpleGrantedAuthority("USER");
        return new UserAuth(uuid, username, password, Collections.singleton(role));
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}