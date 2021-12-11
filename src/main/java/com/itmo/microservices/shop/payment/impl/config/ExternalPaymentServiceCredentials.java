package com.itmo.microservices.shop.payment.impl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("external-payment-service")
@Component
public class ExternalPaymentServiceCredentials {
    private String url;
    private String syncSecret;
    private String pollingSecret;
    private Integer rateLimit;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSyncSecret() {
        return syncSecret;
    }

    public void setSyncSecret(String syncSecret) {
        this.syncSecret = syncSecret;
    }

    public String getPollingSecret() {
        return pollingSecret;
    }

    public void setPollingSecret(String pollingSecret) {
        this.pollingSecret = pollingSecret;
    }

    public Integer getRateLimit() { return rateLimit; }

    public void setRateLimit(Integer rateLimit) { this.rateLimit = rateLimit; }
}