package com.itmo.microservices.shop.user.impl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ConfigurationProperties("security")
@Component
public class SecurityProperties {
    public String secret = "sec12345678";
    public Duration tokenLifeTime = Duration.ofMinutes(15);
    public Duration refreshTokenLifeTime = Duration.ofMinutes(30);

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
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
