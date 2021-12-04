package com.itmo.microservices.shop.common.transactions.exception;

public class TransactionUpdateException extends TransactionProcessingException {
    public TransactionUpdateException() {
    }

    public TransactionUpdateException(String message) {
        super(message);
    }

    public TransactionUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionUpdateException(Throwable cause) {
        super(cause);
    }
}
