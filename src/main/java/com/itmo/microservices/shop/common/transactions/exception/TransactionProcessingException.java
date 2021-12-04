package com.itmo.microservices.shop.common.transactions.exception;

public class TransactionProcessingException extends Exception {
    public TransactionProcessingException() {
    }

    public TransactionProcessingException(String message) {
        super(message);
    }

    public TransactionProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionProcessingException(Throwable cause) {
        super(cause);
    }
}
