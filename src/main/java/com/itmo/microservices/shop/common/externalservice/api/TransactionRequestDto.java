package com.itmo.microservices.shop.common.externalservice.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionRequestDto {
    private String clientSecret;
}
