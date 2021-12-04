package com.itmo.microservices.shop.common.transactions.exception;

public class TransactionStartException extends TransactionProcessingException {
    public TransactionStartException() {
    }

    public TransactionStartException(String message) {
        super(message);
    }

    public TransactionStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionStartException(Throwable cause) {
        super(cause);
    }
}
