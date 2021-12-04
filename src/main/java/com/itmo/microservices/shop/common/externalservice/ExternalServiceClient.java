package com.itmo.microservices.shop.common.externalservice;

import com.itmo.microservices.shop.common.externalservice.api.TransactionRequestDto;
import com.itmo.microservices.shop.common.externalservice.api.TransactionResponseDto;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class ExternalServiceClient {
    private final String baseUrl;
    private final String clientSecret;
    private final RestTemplate restTemplate = new RestTemplate();

    public ExternalServiceClient(String baseUrl, String clientSecret) {
        this.baseUrl = baseUrl;
        this.clientSecret = clientSecret;
    }

    public TransactionResponseDto post() {
        return restTemplate.postForObject(baseUrl + "/transactions",
                new TransactionRequestDto(clientSecret),
                TransactionResponseDto.class);
    }

    public TransactionResponseDto get(UUID transactionId) {
        return restTemplate.getForObject(baseUrl + "/transactions/" + transactionId, TransactionResponseDto.class);
    }
}
