package com.itmo.microservices.shop.user.impl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

@ConfigurationProperties("security")
@Component
public class SecurityProperties {
    @Value("${security.secret}")
    private String secret;

    @Value("${security.token-lifetime}")
    private int accessTokenLifetimeSeconds;

    @Value("${security.refresh-token-lifetime}")
    private int refreshTokenLifetimeSeconds;

    private Duration tokenLifeTime = Duration.ofSeconds(accessTokenLifetimeSeconds);
    private Duration refreshTokenLifeTime = Duration.ofSeconds(refreshTokenLifetimeSeconds);

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
