package com.itmo.microservices.shop.common.transactions.functional;

import com.itmo.microservices.shop.common.transactions.TransactionWrapper;
import com.itmo.microservices.shop.common.transactions.exception.TransactionProcessingException;

@FunctionalInterface
public interface TransactionProcessor<T, ID, C> {
    TransactionWrapper<T, ID> startTransaction(C context, Object... args) throws TransactionProcessingException;
}
