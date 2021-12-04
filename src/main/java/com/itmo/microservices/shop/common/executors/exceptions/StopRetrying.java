package com.itmo.microservices.shop.common.executors.exceptions;

public class StopRetrying extends Exception {
    public StopRetrying() {
    }

    public StopRetrying(String message) {
        super(message);
    }

    public StopRetrying(String message, Throwable cause) {
        super(message, cause);
    }

    public StopRetrying(Throwable cause) {
        super(cause);
    }
}
