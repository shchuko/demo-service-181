package com.itmo.microservices.shop.user.impl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ConfigurationProperties("security")
@Component
public class SecurityProperties {
    private String secret;
    private String adminSecret;
    private Duration tokenLifeTime;
    private Duration refreshTokenLifeTime;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAdminSecret() {
        return adminSecret;
    }

    public void setAdminSecret(String adminSecret) {
        this.adminSecret = adminSecret;
    }

    public Duration getTokenLifeTime() {
        return tokenLifeTime;
    }

    public void setTokenLifeTime(Duration tokenLifeTime) {
        this.tokenLifeTime = tokenLifeTime;
    }

    public Duration getRefreshTokenLifeTime() {
        return refreshTokenLifeTime;
    }

    public void setRefreshTokenLifeTime(Duration refreshTokenLifeTime) {
        this.refreshTokenLifeTime = refreshTokenLifeTime;
    }
}
