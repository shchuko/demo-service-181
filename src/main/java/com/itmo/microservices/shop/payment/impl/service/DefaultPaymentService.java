package com.itmo.microservices.shop.payment.impl.service;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.shop.common.executors.RetryingExecutorService;
import com.itmo.microservices.shop.common.executors.TimedRetryingExecutorService;
import com.itmo.microservices.shop.common.executors.timeout.FixedTimeoutProvider;
import com.itmo.microservices.shop.common.executors.timeout.TimeoutProvider;
import com.itmo.microservices.shop.common.externalservice.ExternalServiceClient;
import com.itmo.microservices.shop.common.externalservice.api.TransactionResponseDto;
import com.itmo.microservices.shop.common.limiters.RateLimiter;
import com.itmo.microservices.shop.common.transactions.TransactionPollingProcessor;
import com.itmo.microservices.shop.common.transactions.TransactionSyncProcessor;
import com.itmo.microservices.shop.common.transactions.TransactionWrapper;
import com.itmo.microservices.shop.common.transactions.WriteBackStorage;
import com.itmo.microservices.shop.common.transactions.exception.TransactionProcessingException;
import com.itmo.microservices.shop.common.transactions.exception.TransactionStartException;
import com.itmo.microservices.shop.common.transactions.functional.TransactionProcessor;
import com.itmo.microservices.shop.payment.api.messaging.*;
import com.itmo.microservices.shop.payment.api.model.PaymentLogRecordDto;
import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.shop.payment.api.service.PaymentService;
import com.itmo.microservices.shop.payment.impl.config.ExternalPaymentServiceCredentials;
import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import com.itmo.microservices.shop.payment.impl.entity.PaymentStatus;
import com.itmo.microservices.shop.payment.impl.entity.PaymentTransactionsProcessorWriteback;
import com.itmo.microservices.shop.payment.impl.exceptions.*;
import com.itmo.microservices.shop.payment.impl.logging.PaymentServiceNotableEvent;
import com.itmo.microservices.shop.payment.impl.mapper.Mappers;
import com.itmo.microservices.shop.payment.impl.repository.FinancialOperationTypeRepository;
import com.itmo.microservices.shop.payment.impl.repository.PaymentLogRecordRepository;
import com.itmo.microservices.shop.payment.impl.repository.PaymentStatusRepository;
import com.itmo.microservices.shop.payment.impl.repository.PaymentTransactionsProcessorWritebackRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@Service
public class DefaultPaymentService implements PaymentService {
    private static final long POLLING_RETRY_INTERVAL_MILLIS = 500;
    private static final int RETRYING_EXECUTOR_POOL_SIZE = 5;
    private static final int PAYMENT_SUBMISSION_EXECUTOR_POOL_SIZE = 5;

    private final ExternalServiceClient pollingClient;
    private final ExternalServiceClient syncClient;

    private final TransactionProcessor<TransactionResponseDto, UUID, TransactionContext> syncTransactionProcessor;
    private final TransactionProcessor<TransactionResponseDto, UUID, TransactionContext> pollingTransactionProcessor;

    private final PaymentLogRecordRepository paymentLogRecordRepo;
    private final FinancialOperationTypeRepository financialOperationTypeRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentTransactionsProcessorWritebackRepository paymentTransactionsProcessorWritebackRepository;

    private final Map<SubmittedPaymentKey, SubmittedPaymentValue> submittedPayments = new HashMap<>();

    private final ScheduledThreadPoolExecutor pendingPaymentsProcessingExecutor = new ScheduledThreadPoolExecutor(PAYMENT_SUBMISSION_EXECUTOR_POOL_SIZE);

    @InjectEventLogger
    private EventLogger eventLogger;

    private final EventBus eventBus;

    {
        pendingPaymentsProcessingExecutor.scheduleAtFixedRate(() -> {
                    synchronized (submittedPayments) {
                        var expired = submittedPayments.entrySet().stream()
                                .filter(it -> it.getValue().isExpired())
                                .map(Map.Entry::getKey).collect(Collectors.toList());

                        expired.forEach(it -> {
                            try {
                                cancelPayment(it.userId, it.orderId);
                            } catch (PaymentInfoNotFoundException | PaymentInUninterruptibleProcessing e) {
                                /* TODO eventBus: post */
                            }
                        });
                    }
                },
                0, 5, TimeUnit.SECONDS);
    }

    @Override
    @NotNull
    public PaymentSubmissionDto payForOrder(@NotNull UUID userId, @NotNull UUID orderId) throws PaymentFailedException, PaymentAlreadyExistsException, PaymentInfoNotFoundException {
        var key = new SubmittedPaymentKey(userId, orderId);
        SubmittedPaymentValue value;
        synchronized (submittedPayments) {
            value = submittedPayments.getOrDefault(key, null);
            if (value == null) {
                /* TODO eventBus: post */
                throw new PaymentInfoNotFoundException("Payment for 'orderId=" + orderId + "' not found");
            }

            if (value.nowProcessing) {
                /* TODO eventBus: post */
                throw new PaymentAlreadyExistsException("Payment for orderId='" + orderId + "' already queued for processing");
            }
            value.setNowProcessing(true);
        }

        eventLogger.info(PaymentServiceNotableEvent.I_PAYMENT_REQUEST_ADDED_SUCCESSFULLY, orderId);
        return this.startTransaction(userId, orderId, FinancialOperationTypeRepository.VALUES.WITHDRAW.name(), value.amount);
    }

    @Override
    public void submitPayment(@NotNull UUID userId, @NotNull UUID orderId, int amount, long expirationTimeoutMillis) throws PaymentAlreadyExistsException {
        synchronized (submittedPayments) {
            var key = new SubmittedPaymentKey(userId, orderId);
            if (submittedPayments.containsKey(key)) {
                /* TODO eventBus: post */
                throw new PaymentAlreadyExistsException("Payment for orderId='" + orderId + "' already submitted");
            }
            submittedPayments.put(key, new SubmittedPaymentValue(amount, FinancialOperationTypeRepository.VALUES.WITHDRAW, expirationTimeoutMillis));
        }
    }

    @Override
    public void cancelPayment(@NotNull UUID userId, @NotNull UUID orderId) throws PaymentInfoNotFoundException, PaymentInUninterruptibleProcessing {
        SubmittedPaymentValue value;
        synchronized (submittedPayments) {
            var key = new SubmittedPaymentKey(userId, orderId);
            value = submittedPayments.getOrDefault(key, null);
            if (value == null) {
                /* TODO eventBus: post */
                throw new PaymentInfoNotFoundException("Payment for orderId='" + orderId + "' not found submitted");
            }

            if (value.nowProcessing) {
                /* TODO eventBus: post */
                throw new PaymentInUninterruptibleProcessing("Payment for orderId='" + orderId + "' cannot be unterrupted");
            }
            submittedPayments.remove(new SubmittedPaymentKey(userId, orderId));
        }

        eventBus.post(new PaymentCancelledEvent(orderId, userId, value.operationType.name()));
    }

    @Override
    public List<UserAccountFinancialLogRecordDto> listUserFinLog(UUID userId) {
        return paymentLogRecordRepo.findByUserId(userId).stream().map(Mappers::buildFinLogRecordDto).collect(Collectors.toList());
    }

    @Override
    public List<UserAccountFinancialLogRecordDto> listUserFinLog(UUID userId, UUID orderId) {
        return paymentLogRecordRepo.findByUserIdAndOrderId(userId, orderId).stream().map(Mappers::buildFinLogRecordDto).collect(Collectors.toList());
    }

    @Override
    public List<PaymentLogRecordDto> listOrderPaymentLog(UUID userId, UUID orderId) {
        return paymentLogRecordRepo.findByUserIdAndOrderId(userId, orderId).stream()
                .filter(it -> it.getFinancialOperationType().getName().equals(FinancialOperationTypeRepository.VALUES.WITHDRAW.name())) // TODO ensure that this filter is required
                .map(Mappers::buildPaymentLogRecordDto)
                .collect(Collectors.toList());
    }

    @Subscribe
    @Override
    public void handleRefund(@NotNull RefundRequestEvent event) throws PaymentAlreadyExistsException {
        var userId = event.getUserId();
        var orderId = event.getOrderId();
        var amount = event.getAmount();
        var operationType = FinancialOperationTypeRepository.VALUES.REFUND;

        synchronized (submittedPayments) {
            var key = new SubmittedPaymentKey(userId, orderId);
            var value = submittedPayments.getOrDefault(key, null);
            if (value != null) {
                /* TODO eventBus: post */
                throw new PaymentAlreadyExistsException("Payment for orderId='" + orderId + "' already exists");
            }
            submittedPayments.put(key, new SubmittedPaymentValue(event.getAmount(), operationType, -1, true));
        }

        try {
            /* TODO eventBus: post */
            startTransaction(userId, orderId, operationType.name(), amount);
        } catch (PaymentFailedException e) {
            /* TODO eventBus: post */

        }
    }

    public DefaultPaymentService(PaymentLogRecordRepository paymentLogRecordRepo,
                                 FinancialOperationTypeRepository financialOperationTypeRepository,
                                 PaymentStatusRepository paymentStatusRepository,
                                 ExternalPaymentServiceCredentials externalPaymentServiceCredentials,
                                 PaymentTransactionsProcessorWritebackRepository paymentTransactionsProcessorWritebackRepository,
                                 EventBus eventBus) {
        this.paymentLogRecordRepo = paymentLogRecordRepo;
        this.financialOperationTypeRepository = financialOperationTypeRepository;
        this.paymentStatusRepository = paymentStatusRepository;
        this.paymentTransactionsProcessorWritebackRepository = paymentTransactionsProcessorWritebackRepository;
        this.eventBus = eventBus;

        pollingClient = new ExternalServiceClient(externalPaymentServiceCredentials.getUrl(), externalPaymentServiceCredentials.getPollingSecret());
        syncClient = new ExternalServiceClient(externalPaymentServiceCredentials.getUrl(), externalPaymentServiceCredentials.getSyncSecret());

        TimeoutProvider timeoutProvider = new FixedTimeoutProvider(POLLING_RETRY_INTERVAL_MILLIS);
        RetryingExecutorService retryingExecutorService = new TimedRetryingExecutorService(RETRYING_EXECUTOR_POOL_SIZE, timeoutProvider);
        syncTransactionProcessor = new TransactionSyncProcessor<>((Object... ignored) -> syncClient.post().toTransactionWrapper());

        pollingTransactionProcessor = TransactionPollingProcessor.<TransactionResponseDto, UUID, TransactionContext>builder()
                .withPollingExecutorService(retryingExecutorService)
                .withWriteBackStorage(new WriteBackStorageImpl())
                .withTransactionStartLimiter(new RateLimiter(externalPaymentServiceCredentials.getRateLimit(), TimeUnit.MINUTES))
                .withTransactionStarter((Object... ignored) -> {
                    try {
                        return pollingClient.post().toTransactionWrapper();
                    } catch (HttpServerErrorException | HttpClientErrorException e) {
                        throw new TransactionStartException(e);
                    }
                }).withTransactionUpdater((UUID transactionId) -> pollingClient.get(transactionId).toTransactionWrapper())
                .withTransactionFinishHandler(this::transactionCompletionProcessor)
                // External service issue, just retry on 404 until SUCCESS received
                // See https://t.me/c/1436658303/1445
                // TODO validate that fixed and remove
                .withIgnoringExceptionHandler(HttpClientErrorException.NotFound.class).build();
    }

    @NotNull
    private PaymentSubmissionDto startTransaction(@NotNull UUID userId, @NotNull UUID orderId, @NotNull String operationType, int amount) throws PaymentFailedException {
        TransactionContext context = new TransactionContext(new PaymentTransactionsProcessorWriteback(amount, orderId, userId, operationType));

        /* Try to submit transaction with polling */
        TransactionWrapper<TransactionResponseDto, UUID> transactionWrapper = null;
        try {
            transactionWrapper = pollingTransactionProcessor.startTransaction(context);
        } catch (TransactionProcessingException ignored) {
            /* TODO collect metrics here */
            /* TODO eventBus: post */
        }

        if (transactionWrapper == null) {
            /* On error retry using synchronous call */
            try {
                transactionWrapper = syncTransactionProcessor.startTransaction(context);
            } catch (TransactionProcessingException e) {
                synchronized (submittedPayments) {
                    var value = submittedPayments.getOrDefault(new SubmittedPaymentKey(userId, orderId), null);
                    assert (value != null);
                    if (value != null) {
                        value.setNowProcessing(false);
                    }
                }

                /* TODO collect metrics here */
                /* TODO eventBus: post */

                throw new PaymentFailedException("Payment failed because of external service error");
            }
            transactionCompletionProcessor(transactionWrapper, context);
        }

        return new PaymentSubmissionDto(transactionWrapper.getWrappedObject().getSubmitTime(), transactionWrapper.getId());
    }

    private void transactionCompletionProcessor(TransactionWrapper<TransactionResponseDto, UUID> transaction, TransactionContext context) {
        var orderId = context.unwrap().getOrderId();
        var userId = context.unwrap().getUserId();
        var transactionId = transaction.getId();
        var amount = context.unwrap().getAmount();
        var opTypeName = context.unwrap().getFinancialOperationTypeName();
        var opType = financialOperationTypeRepository.findFinancialOperationTypeByName(opTypeName);
        var completedTime = transaction.getWrappedObject().getCompletedTime();

        PaymentStatus status = null;
        PaymentStatusEvent event = null;
        switch (transaction.getStatus()) {
            case SUCCESS:
                event = new PaymentSuccessfulEvent(orderId, userId, opTypeName);
                status = paymentStatusRepository.findByName(PaymentStatusRepository.VALUES.SUCCESS.name());
                synchronized (submittedPayments) {
                    var removed = submittedPayments.remove(new SubmittedPaymentKey(userId, orderId));
                    assert (removed != null);
                }
                break;
            case FAILURE:
                event = new PaymentFailedEvent(orderId, userId, opTypeName);
                status = paymentStatusRepository.findByName(PaymentStatusRepository.VALUES.FAILED.name());
                synchronized (submittedPayments) {
                    var value = submittedPayments.getOrDefault(new SubmittedPaymentKey(userId, orderId), null);
                    assert (value != null);
                    value.setNowProcessing(false);
                }
                break;
            case PENDING:
                throw new IllegalStateException("Should not be reached");
        }

        var paymentLogRecord = new PaymentLogRecord(amount, completedTime, orderId, transactionId, userId, status, opType);
        paymentLogRecordRepo.save(paymentLogRecord);

        eventBus.post(event);
    }

    private static class TransactionContext {
        private final PaymentTransactionsProcessorWriteback paymentTransactionsProcessorWriteback;

        private TransactionContext(PaymentTransactionsProcessorWriteback paymentTransactionsProcessorWriteback) {
            this.paymentTransactionsProcessorWriteback = paymentTransactionsProcessorWriteback;
        }

        private PaymentTransactionsProcessorWriteback unwrap() {
            return paymentTransactionsProcessorWriteback;
        }
    }

    private class WriteBackStorageImpl implements WriteBackStorage<UUID, TransactionContext> {
        @Override
        public void add(UUID uuid, TransactionContext context) {
            context.unwrap().setId(uuid);
            paymentTransactionsProcessorWritebackRepository.save(context.unwrap());
        }

        @Override
        public void remove(UUID uuid) {
            try {
                paymentTransactionsProcessorWritebackRepository.deleteById(uuid);
            } catch (Exception ignored) {
            }
        }

        @Override
        public List<Map.Entry<UUID, TransactionContext>> getAll() {
            return new ArrayList<>(paymentTransactionsProcessorWritebackRepository.findAll().stream().collect(Collectors.toMap(PaymentTransactionsProcessorWriteback::getId, TransactionContext::new)).entrySet());
        }
    }


    /**
     * TODO create backing table in DB and make the service shutdown-tolerant
     */
    private static class SubmittedPaymentKey {
        @NotNull
        final UUID userId;
        @NotNull
        final UUID orderId;

        public SubmittedPaymentKey(@NotNull UUID userId, @NotNull UUID orderId) {
            this.userId = userId;
            this.orderId = orderId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SubmittedPaymentKey that = (SubmittedPaymentKey) o;
            return userId.equals(that.userId) && orderId.equals(that.orderId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, orderId);
        }

    }

    private static class SubmittedPaymentValue {
        final int amount;

        @NotNull
        final FinancialOperationTypeRepository.VALUES operationType;

        boolean nowProcessing;

        private final long expiresOnNanos;
        private final boolean neverExpire;

        SubmittedPaymentValue(int amount, @NotNull FinancialOperationTypeRepository.VALUES operationType, long expirationTimeMillis, boolean nowProcessing) {
            this.neverExpire = expirationTimeMillis < 0;
            this.amount = amount;
            this.operationType = operationType;
            this.nowProcessing = nowProcessing;
            this.expiresOnNanos = System.nanoTime() + expirationTimeMillis * 1000000;
        }

        SubmittedPaymentValue(int amount, @NotNull FinancialOperationTypeRepository.VALUES operationType, long expirationTimeMillis) {
            this(amount, operationType, expirationTimeMillis, false);
        }

        void setNowProcessing(boolean value) {
            this.nowProcessing = value;
        }

        boolean isExpired() {
            if (neverExpire) {
                return false;
            }
            return System.nanoTime() > expiresOnNanos;
        }
    }
}
