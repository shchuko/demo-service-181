package com.itmo.microservices.shop.user.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmo.microservices.shop.user.impl.userdetails.UserAuth;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.UUID;

public class UserModel {
    @JsonProperty("id")
    private UUID uuid;

    @JsonProperty("name")
    private String username;

    @JsonIgnore
    private Boolean admin;

    @JsonIgnore
    private String password;

    public UserModel(UUID uuid, String username, String password, Boolean admin) {
        this.uuid = uuid;
        this.username = username;
        this.admin = admin;
        this.password = password;
    }

    public UserAuth userDetails() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        if (admin) {
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }
        return new UserAuth(uuid, username, password, authorities);
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

    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
