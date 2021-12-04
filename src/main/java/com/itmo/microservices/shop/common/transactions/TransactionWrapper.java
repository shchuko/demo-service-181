package com.itmo.microservices.shop.common.transactions;


public interface TransactionWrapper<T, ID> {
    T getWrappedObject();

    ID getId();

    TransactionStatus getStatus();
}
