package com.itmo.microservices.shop.user.impl.userdetails;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;


public class UserAuth extends User {
    private final UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    public UserAuth(UUID uuid, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.uuid = uuid;
    }
}
