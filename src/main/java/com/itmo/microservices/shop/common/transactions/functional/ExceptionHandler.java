package com.itmo.microservices.shop.common.transactions.functional;

import com.itmo.microservices.shop.common.transactions.exception.StopPolling;

@FunctionalInterface
public interface ExceptionHandler {
    void handle(Throwable exception) throws StopPolling;
}
