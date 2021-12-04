package com.itmo.microservices.shop.common.executors;

import com.itmo.microservices.shop.common.executors.exceptions.StopRetrying;

@FunctionalInterface
public interface RetryTask {
     void doTask() throws StopRetrying;
}
