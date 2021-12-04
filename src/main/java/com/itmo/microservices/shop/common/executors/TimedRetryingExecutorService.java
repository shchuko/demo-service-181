package com.itmo.microservices.shop.common.executors;

import com.itmo.microservices.shop.common.executors.exceptions.StopRetrying;
import com.itmo.microservices.shop.common.executors.timeout.TimeoutProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimedRetryingExecutorService implements RetryingExecutorService {
    private final ScheduledExecutorService scheduledExecutorService;
    private final TimeoutProvider timeoutProvider;

    public TimedRetryingExecutorService(int corePoolSize, TimeoutProvider timeoutProvider) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize);
        this.timeoutProvider = timeoutProvider;
    }

    @Override
    public void submitTask(RetryTask task) {
        submitTaskInternal(task, timeoutProvider.nextTimeoutMillis());
    }

    private void submitTaskInternal(RetryTask task, long timeoutMillis) {
        scheduledExecutorService.schedule(() -> {
            try {
                task.doTask();
            } catch (StopRetrying ignored) {
                return;
            }

            if (timeoutProvider.hasNextTimeout()) {
                submitTaskInternal(task, timeoutProvider.nextTimeoutMillis());
            }
        }, timeoutMillis, TimeUnit.MILLISECONDS);
    }
}
