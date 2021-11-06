package com.itmo.microservices.shop.user.impl.userdetails;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;


public class UserAuth extends User {
    private final UUID uuid;
    private final String email;

    public UUID getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public UserAuth(UUID uuid, String username, String password, String email, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.uuid = uuid;
        this.email = email;
    }
}
