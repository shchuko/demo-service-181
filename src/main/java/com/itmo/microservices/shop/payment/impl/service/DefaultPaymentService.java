package com.itmo.microservices.shop.payment.impl.service;


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
import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.api.service.PaymentService;
import com.itmo.microservices.shop.payment.impl.config.ExternalPaymentServiceCredentials;
import com.itmo.microservices.shop.payment.impl.entity.PaymentLogRecord;
import com.itmo.microservices.shop.payment.impl.entity.PaymentTransactionsProcessorWriteback;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentFailedException;
import com.itmo.microservices.shop.payment.impl.repository.FinancialOperationTypeRepository;
import com.itmo.microservices.shop.payment.impl.repository.PaymentLogRecordRepository;
import com.itmo.microservices.shop.payment.impl.repository.PaymentStatusRepository;
import com.itmo.microservices.shop.payment.impl.repository.PaymentTransactionsProcessorWritebackRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DefaultPaymentService implements PaymentService {
    private static final long POLLING_RETRY_INTERVAL_MILLIS = 500;
    private static final int RETRYING_EXECUTOR_POOL_SIZE = 5;

    private final ExternalServiceClient pollingClient;
    private final ExternalServiceClient syncClient;

    private final TransactionProcessor<TransactionResponseDto, UUID, TransactionContext> syncTransactionProcessor;
    private final TransactionProcessor<TransactionResponseDto, UUID, TransactionContext> pollingTransactionProcessor;

    private final PaymentLogRecordRepository paymentLogRecordRepo;
    private final FinancialOperationTypeRepository financialOperationTypeRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentTransactionsProcessorWritebackRepository paymentTransactionsProcessorWritebackRepository;

    public DefaultPaymentService(PaymentLogRecordRepository paymentLogRecordRepo,
                                 FinancialOperationTypeRepository financialOperationTypeRepository,
                                 PaymentStatusRepository paymentStatusRepository,
                                 ExternalPaymentServiceCredentials externalPaymentServiceCredentials,
                                 PaymentTransactionsProcessorWritebackRepository paymentTransactionsProcessorWritebackRepository) {
        this.paymentLogRecordRepo = paymentLogRecordRepo;
        this.financialOperationTypeRepository = financialOperationTypeRepository;
        this.paymentStatusRepository = paymentStatusRepository;
        this.paymentTransactionsProcessorWritebackRepository = paymentTransactionsProcessorWritebackRepository;

        pollingClient = new ExternalServiceClient(externalPaymentServiceCredentials.getUrl(), externalPaymentServiceCredentials.getPollingSecret());
        syncClient = new ExternalServiceClient(externalPaymentServiceCredentials.getUrl(), externalPaymentServiceCredentials.getSyncSecret());

        TimeoutProvider timeoutProvider = new FixedTimeoutProvider(POLLING_RETRY_INTERVAL_MILLIS);
        RetryingExecutorService retryingExecutorService = new TimedRetryingExecutorService(RETRYING_EXECUTOR_POOL_SIZE, timeoutProvider);
        syncTransactionProcessor = new TransactionSyncProcessor<>((Object... ignored) -> syncClient.post().toTransactionWrapper());

        pollingTransactionProcessor = TransactionPollingProcessor.<TransactionResponseDto, UUID, TransactionContext>builder()
                .withPollingExecutorService(retryingExecutorService)
                .withWriteBackStorage(new WriteBackStorageImpl())
                .withTransactionStartLimiter(new RateLimiter(externalPaymentServiceCredentials.getRateLimit(),
                        TimeUnit.MINUTES))
                .withTransactionStarter((Object... ignored) -> {
                    try {
                        return pollingClient.post().toTransactionWrapper();
                    } catch (HttpServerErrorException | HttpClientErrorException e) {
                        throw new TransactionStartException(e);
                    }
                })
                .withTransactionUpdater((UUID transactionId) -> pollingClient.get(transactionId).toTransactionWrapper())
                .withTransactionFinishHandler(this::transactionCompletionProcessor)
                // External service issue, just retry on 404 until SUCCESS received
                // See https://t.me/c/1436658303/1445
                .withIgnoringExceptionHandler(HttpClientErrorException.NotFound.class)
                .build();
    }

    public PaymentSubmissionDto orderPayment(String orderId) throws PaymentFailedException {

        PaymentTransactionsProcessorWriteback entry = new PaymentTransactionsProcessorWriteback();

        // TODO move user id setting to the payment controller, get it this method argument
        entry.setUserId(UUID.fromString("E99B7EE6-EE5E-4CB9-9CDD-33FE50765E6E"));
        entry.setFinancialOperationTypeName(FinancialOperationTypeRepository.VALUES.WITHDRAW.name());

        entry.setOrderId(UUID.fromString(orderId)); // TODO add order id validation
        entry.setAmount(10); // TODO remove order amount stub

        TransactionContext context = new TransactionContext(entry);
        /* Try to submit transaction using polling */
        TransactionWrapper<TransactionResponseDto, UUID> transactionWrapper = null;
        try {
            transactionWrapper = pollingTransactionProcessor.startTransaction(context);
        } catch (TransactionProcessingException ignored) {
            // TODO collect metrics here
        }

        /* On error retry using synchronous call */
        if (transactionWrapper == null) {
            try {
                transactionWrapper = syncTransactionProcessor.startTransaction(context);
            } catch (TransactionProcessingException e) {
                // TODO collect metrics here
                throw new PaymentFailedException("Payment failed because of external service error");
            }
            transactionCompletionProcessor(transactionWrapper, context);
        }

        return new PaymentSubmissionDto(transactionWrapper.getWrappedObject().getSubmitTime(), transactionWrapper.getId());
    }

    private void transactionCompletionProcessor(TransactionWrapper<TransactionResponseDto, UUID> transaction, TransactionContext context) {
        PaymentLogRecord paymentLogRecord = new PaymentLogRecord();

        paymentLogRecord.setTransactionId(transaction.getId());
        paymentLogRecord.setAmount(context.unwrap().getAmount());
        paymentLogRecord.setOrderId(context.unwrap().getOrderId());
        paymentLogRecord.setUserId(context.unwrap().getUserId());
        paymentLogRecord.setFinancialOperationType(financialOperationTypeRepository.findFinancialOperationTypeByName(context.unwrap().getFinancialOperationTypeName()));
        paymentLogRecord.setTimestamp(transaction.getWrappedObject().getCompletedTime());

        switch (transaction.getStatus()) {
            case SUCCESS:
                paymentLogRecord.setPaymentStatus(paymentStatusRepository.findByName(PaymentStatusRepository.VALUES.SUCCESS.name()));
                break;
            case FAILURE:
                paymentLogRecord.setPaymentStatus(paymentStatusRepository.findByName(PaymentStatusRepository.VALUES.FAILED.name()));
                break;
        }

        paymentLogRecordRepo.saveAndFlush(paymentLogRecord);
        // TODO notify order service about order payment completion
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
}
