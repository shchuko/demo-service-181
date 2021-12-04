package com.itmo.microservices.shop.common.transactions.functional;

import com.itmo.microservices.shop.common.transactions.TransactionWrapper;
import com.itmo.microservices.shop.common.transactions.exception.TransactionStartException;

@FunctionalInterface
public interface TransactionStarter<T, ID> {
    TransactionWrapper<T, ID> start(Object... args) throws TransactionStartException;
}
