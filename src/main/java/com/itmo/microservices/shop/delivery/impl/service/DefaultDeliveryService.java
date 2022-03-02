package com.itmo.microservices.shop.delivery.impl.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.commonlib.logging.NotableEvent;
import com.itmo.microservices.shop.common.executors.RetryingExecutorService;
import com.itmo.microservices.shop.common.executors.TimedRetryingExecutorService;
import com.itmo.microservices.shop.common.executors.timeout.FixedTimeoutProvider;
import com.itmo.microservices.shop.common.executors.timeout.TimeoutProvider;
import com.itmo.microservices.shop.common.externalservice.ExternalServiceClient;
import com.itmo.microservices.shop.common.externalservice.api.TransactionResponseDto;
import com.itmo.microservices.shop.common.limiters.RateLimiter;
import com.itmo.microservices.shop.common.metrics.MetricCollector;
import com.itmo.microservices.shop.common.transactions.TransactionPollingProcessor;
import com.itmo.microservices.shop.common.transactions.TransactionSyncProcessor;
import com.itmo.microservices.shop.common.transactions.TransactionWrapper;
import com.itmo.microservices.shop.common.transactions.WriteBackStorage;
import com.itmo.microservices.shop.common.transactions.exception.TransactionProcessingException;
import com.itmo.microservices.shop.common.transactions.exception.TransactionStartException;
import com.itmo.microservices.shop.common.transactions.functional.TransactionProcessor;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryStatusFailedEvent;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryStatusSuccessEvent;
import com.itmo.microservices.shop.delivery.api.messaging.StartDeliveryEvent;
import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoRecordDto;
import com.itmo.microservices.shop.delivery.api.service.DeliveryService;
import com.itmo.microservices.shop.delivery.impl.config.ExternalDeliveryServiceCredentials;
import com.itmo.microservices.shop.delivery.impl.entity.DeliveryTransactionsProcessorWriteback;
import com.itmo.microservices.shop.delivery.impl.metrics.DeliveryMetricEvent;
import com.itmo.microservices.shop.delivery.impl.repository.DeliveryTransactionsProcessorWritebackRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itmo.microservices.shop.delivery.impl.logging.DeliveryServiceNotableEvents.DELIVERY_SLOTS_REQUESTED;
import static com.itmo.microservices.shop.delivery.impl.logging.DeliveryServiceNotableEvents.DELIVERY_SLOTS_REQUESTED_LIMITED;

@Service
@SuppressWarnings("UnstableApiUsage")
public class DefaultDeliveryService implements DeliveryService {
    private static final long POLLING_RETRY_INTERVAL_MILLIS = 500;
    private static final int RETRYING_EXECUTOR_POOL_SIZE = 5;

    private final ExternalServiceClient pollingClient;
    private final ExternalServiceClient syncClient;

    private final TransactionProcessor<TransactionResponseDto, UUID, TransactionContext> syncTransactionProcessor;
    private final TransactionProcessor<TransactionResponseDto, UUID, TransactionContext> pollingTransactionProcessor;

    private final DeliveryTransactionsProcessorWritebackRepository writebackRepository;

    /* TODO store data in DB */
    private final List<DeliveryInfoRecordDto> deliveryLogsStub = new ArrayList<>();

    @InjectEventLogger
    private EventLogger eventLogger;
    private final EventBus eventBus;

    private final MetricCollector metricCollector;

    public DefaultDeliveryService(DeliveryTransactionsProcessorWritebackRepository writebackRepository,
                                  ExternalDeliveryServiceCredentials externalDeliveryServiceCredentials,
                                  EventBus eventBus, MetricCollector metricCollector) {
        this.writebackRepository = writebackRepository;
        this.eventBus = eventBus;
        this.metricCollector = metricCollector;
        metricCollector.register(DeliveryMetricEvent.values());

        pollingClient = new ExternalServiceClient(externalDeliveryServiceCredentials.getUrl(), externalDeliveryServiceCredentials.getPollingSecret());
        syncClient = new ExternalServiceClient(externalDeliveryServiceCredentials.getUrl(), externalDeliveryServiceCredentials.getSyncSecret());

        TimeoutProvider timeoutProvider = new FixedTimeoutProvider(POLLING_RETRY_INTERVAL_MILLIS);
        RetryingExecutorService retryingExecutorService = new TimedRetryingExecutorService(RETRYING_EXECUTOR_POOL_SIZE, timeoutProvider);
        syncTransactionProcessor = new TransactionSyncProcessor<>((Object... ignored) -> syncClient.post().toTransactionWrapper());

        pollingTransactionProcessor = TransactionPollingProcessor.<TransactionResponseDto, UUID, TransactionContext>builder()
                .withPollingExecutorService(retryingExecutorService)
                .withWriteBackStorage(new WriteBackStorageImpl())
                .withTransactionStartLimiter(new RateLimiter(externalDeliveryServiceCredentials.getRateLimit(), TimeUnit.MINUTES))
                .withTransactionStarter((Object... ignored) -> {
                    try {
                        var transactionWrapper = pollingClient.post().toTransactionWrapper();
                        metricCollector.passEvent(DeliveryMetricEvent.DELIVERY_EXTERNAL_EXECUTOR_ACTIVE_TASKS, 1, "deliveryPolling");
                        return transactionWrapper;
                    } catch (HttpServerErrorException | HttpClientErrorException e) {
                        throw new TransactionStartException(e);
                    }
                })
                .withTransactionUpdater((UUID transactionId) -> pollingClient.get(transactionId).toTransactionWrapper())
                .withTransactionFinishHandler((transaction, context) -> {
                    metricCollector.passEvent(DeliveryMetricEvent.DELIVERY_EXTERNAL_EXECUTOR_ACTIVE_TASKS, -1, "deliveryPolling");
                    transactionCompletionProcessor(transaction, context);
                })
                .withExceptionHandler(HttpServerErrorException.InternalServerError.class, (Throwable e) -> metricCollector.passEvent(DeliveryMetricEvent.DELIVERY_EXTERNAL_REQUESTS, 1, "500", "false", "false"))
                .withExceptionHandler(HttpClientErrorException.TooManyRequests.class, (Throwable e) -> metricCollector.passEvent(DeliveryMetricEvent.DELIVERY_EXTERNAL_REQUESTS, 1, "429", "false", "false"))
                .withExceptionHandler(HttpClientErrorException.Unauthorized.class, (Throwable e) -> metricCollector.passEvent(DeliveryMetricEvent.DELIVERY_EXTERNAL_REQUESTS, 1, "401", "false", "false"))
                // External service issue, just retry on 404 until SUCCESS received
                // See https://t.me/c/1436658303/1445
                .withIgnoringExceptionHandler(HttpClientErrorException.NotFound.class)
                .build();
    }

    @Subscribe
    @Override
    public void handleStartDelivery(@NotNull StartDeliveryEvent event) {
        DeliveryTransactionsProcessorWriteback entry = new DeliveryTransactionsProcessorWriteback();

        entry.setOrderId(event.getOrderID());
        entry.setUserId(event.getUserID());
        entry.setTimeSlot(event.getTimeSlot());

        TransactionContext context = new TransactionContext(entry);
        TransactionWrapper<TransactionResponseDto, UUID> transactionWrapper = null;

        metricCollector.passEvent(DeliveryMetricEvent.SHIPPING_ORDERS_TOTAL, 1);
        try {
            metricCollector.passEvent(DeliveryMetricEvent.CURRENT_SHIPPING_ORDERS, 1);
            transactionWrapper = pollingTransactionProcessor.startTransaction(context);
            metricCollector.passEvent(DeliveryMetricEvent.DELIVERY_EXTERNAL_REQUESTS, 1);
        } catch (TransactionProcessingException ignored) {
            // TODO collect metrics here
            metricCollector.passEvent(DeliveryMetricEvent.CURRENT_SHIPPING_ORDERS, -1);
        }

        if (transactionWrapper == null) {
            try {
                metricCollector.passEvent(DeliveryMetricEvent.CURRENT_SHIPPING_ORDERS, 1);

                transactionWrapper = syncTransactionProcessor.startTransaction(context);
            } catch (TransactionProcessingException e) {
                // TODO collect metrics here
                eventBus.post(new DeliveryStatusFailedEvent(entry.getOrderId(),
                        entry.getUserId(),
                        entry.getTimeSlot()));
                metricCollector.passEvent(DeliveryMetricEvent.CURRENT_SHIPPING_ORDERS, -1);
            }
            if (transactionWrapper != null) {
                transactionCompletionProcessor(transactionWrapper, context);
            }
        }
    }

    @Override
    public List<DeliveryInfoRecordDto> getDeliveryLog(UUID orderId) {
        return deliveryLogsStub.stream().filter(it -> it.getOrderId().equals(orderId)).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<Integer> getDeliverySlots(int number) {
        logInfoEvent(DELIVERY_SLOTS_REQUESTED_LIMITED, "" + number);
        return getDeliverySlots().stream().limit(number).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<Integer> getDeliverySlots() {
        logInfoEvent(DELIVERY_SLOTS_REQUESTED);
        /* Stub: Generate numbers 1..30 */
        return Stream.iterate(1, n -> n + 1)
                .limit(30)
                .collect(Collectors.toList());
    }

    private void transactionCompletionProcessor(TransactionWrapper<TransactionResponseDto, UUID> transaction, TransactionContext context) {
        switch (transaction.getStatus()) {
            case SUCCESS:
                /* TODO get rid of Long.valueOf().intValue() */
                int duration = Long.valueOf(transaction.getWrappedObject().getCompletedTime() - transaction.getWrappedObject().getSubmitTime()).intValue();

                /* TODO write to DB */
                deliveryLogsStub.add(new DeliveryInfoRecordDto(
                        DeliveryInfoRecordDto.Outcome.SUCCESS,
                        transaction.getWrappedObject().getSubmitTime(),
                        1,
                        transaction.getWrappedObject().getCompletedTime(),
                        transaction.getId(),
                        transaction.getWrappedObject().getCompletedTime(),
                        context.deliveryTransactionsProcessorWriteback.getOrderId()));

                eventBus.post(new DeliveryStatusSuccessEvent(context.deliveryTransactionsProcessorWriteback.getOrderId(),
                        context.deliveryTransactionsProcessorWriteback.getUserId(),
                        context.deliveryTransactionsProcessorWriteback.getTimeSlot(),
                        duration));
                metricCollector.passEvent(DeliveryMetricEvent.CURRENT_SHIPPING_ORDERS, -1);

                // TODO
                metricCollector.passEvent(DeliveryMetricEvent.DELIVERY_EXTERNAL_REQUESTS, 1, "200", "false", "false");
                break;
            case FAILURE:

                /* TODO write to DB */
                deliveryLogsStub.add(new DeliveryInfoRecordDto(
                        DeliveryInfoRecordDto.Outcome.SUCCESS,
                        transaction.getWrappedObject().getSubmitTime(),
                        1,
                        transaction.getWrappedObject().getCompletedTime(),
                        transaction.getId(),
                        transaction.getWrappedObject().getCompletedTime(),
                        context.deliveryTransactionsProcessorWriteback.getOrderId()));

                eventBus.post(new DeliveryStatusFailedEvent(context.deliveryTransactionsProcessorWriteback.getOrderId(),
                        context.deliveryTransactionsProcessorWriteback.getUserId(),
                        context.deliveryTransactionsProcessorWriteback.getTimeSlot()));
                metricCollector.passEvent(DeliveryMetricEvent.CURRENT_SHIPPING_ORDERS, -1);

                // TODO
                metricCollector.passEvent(DeliveryMetricEvent.DELIVERY_EXTERNAL_REQUESTS, 1, "200", "false", "false");
                break;
        }
    }

    private void logInfoEvent(@NotNull NotableEvent event, @NotNull Object... payload) {
        if (eventLogger != null) {
            eventLogger.info(event, payload);
        }
    }

    private static class TransactionContext {
        private final DeliveryTransactionsProcessorWriteback deliveryTransactionsProcessorWriteback;

        private TransactionContext(DeliveryTransactionsProcessorWriteback deliveryTransactionsProcessorWriteback) {
            this.deliveryTransactionsProcessorWriteback = deliveryTransactionsProcessorWriteback;
        }

        private DeliveryTransactionsProcessorWriteback unwrap() {
            return this.deliveryTransactionsProcessorWriteback;
        }
    }

    private class WriteBackStorageImpl implements WriteBackStorage<UUID, TransactionContext> {
        @Override
        public void add(UUID id, TransactionContext context) {
            context.unwrap().setId(id);
            writebackRepository.save(context.unwrap());
        }

        @Override
        public void remove(UUID id) {
            try {
                writebackRepository.deleteById(id);
            } catch (Exception ignored) {
            }
        }

        @Override
        public List<Map.Entry<UUID, TransactionContext>> getAll() {
            return new ArrayList<>(writebackRepository.findAll()
                    .stream()
                    .collect(Collectors
                            .toMap(DeliveryTransactionsProcessorWriteback::getId,
                                    TransactionContext::new))
                    .entrySet()
            );
        }
    }
}
