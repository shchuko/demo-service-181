package com.itmo.microservices.shop.common.externalservice.api;

import com.itmo.microservices.shop.common.transactions.TransactionStatus;
import com.itmo.microservices.shop.common.transactions.TransactionWrapper;
import lombok.Data;

import java.util.UUID;

@Data
public class TransactionResponseDto {
    private UUID id;
    private String status;
    private Long submitTime;
    private Long completedTime;
    private Integer cost;
    private Integer delta;

    public TransactionWrapper<TransactionResponseDto, UUID> toTransactionWrapper() {
        TransactionResponseDto thisRef = this;
        return new TransactionWrapper<>() {
            @Override
            public TransactionResponseDto getWrappedObject() {
                return thisRef;
            }

            @Override
            public UUID getId() {
                return thisRef.getId();
            }

            @Override
            public TransactionStatus getStatus() {
                switch (thisRef.getStatus()) {
                    case "PENDING":
                        return TransactionStatus.PENDING;
                    case "SUCCESS":
                        return TransactionStatus.SUCCESS;
                    case "FAILURE":
                        return TransactionStatus.FAILURE;
                    default:
                        throw new IllegalStateException();
                }
            }
        };
    }
}
