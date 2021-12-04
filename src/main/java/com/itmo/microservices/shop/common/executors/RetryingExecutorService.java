package com.itmo.microservices.shop.common.executors;

public interface RetryingExecutorService
{
    void submitTask(RetryTask task);
}
