package com.itmo.microservices.shop.payment.api.messaging;

import com.itmo.microservices.shop.payment.impl.entity.RefundOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class RefundOrderAnswerEvent {
    @NotNull
    private UUID orderUUID;
    @NotNull
    private String refundStatus;
}
