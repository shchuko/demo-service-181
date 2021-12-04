package com.itmo.microservices.shop.common.transactions;


import com.itmo.microservices.shop.common.transactions.exception.TransactionStartException;
import com.itmo.microservices.shop.common.transactions.functional.TransactionProcessor;
import com.itmo.microservices.shop.common.transactions.functional.TransactionStarter;

public class TransactionSyncProcessor<T, ID, C> implements TransactionProcessor<T, ID, C> {
    private final TransactionStarter<T, ID> transactionStarter;

    public TransactionSyncProcessor(TransactionStarter<T, ID> transactionStarter) {
        this.transactionStarter = transactionStarter;
    }

    @Override
    public TransactionWrapper<T, ID> startTransaction(C context, Object... args) throws TransactionStartException {
        return transactionStarter.start(args);
    }
}
