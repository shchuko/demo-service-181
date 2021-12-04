package com.itmo.microservices.shop.common.transactions.exception;

public class StopPolling extends Exception {
    public StopPolling() {
    }

    public StopPolling(String message) {
        super(message);
    }

    public StopPolling(String message, Throwable cause) {
        super(message, cause);
    }

    public StopPolling(Throwable cause) {
        super(cause);
    }
}
