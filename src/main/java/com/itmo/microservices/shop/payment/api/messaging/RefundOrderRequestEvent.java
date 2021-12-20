package com.itmo.microservices.shop.payment.api.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class RefundOrderRequestEvent {
    @NotNull
    private UUID orderUUID;
    @NotNull
    private Double price;
}
