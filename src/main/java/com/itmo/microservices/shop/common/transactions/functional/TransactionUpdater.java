package com.itmo.microservices.shop.common.transactions.functional;

import com.itmo.microservices.shop.common.transactions.TransactionWrapper;
import com.itmo.microservices.shop.common.transactions.exception.TransactionUpdateException;

@FunctionalInterface
public interface TransactionUpdater<T, ID> {
    TransactionWrapper<T, ID> update(ID transactionId) throws TransactionUpdateException;
}
