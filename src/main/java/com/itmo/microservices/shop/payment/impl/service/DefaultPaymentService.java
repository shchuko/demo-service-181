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
import com.itmo.microservices.shop.order.api.messaging.OrderFailedPaidEvent;
import com.itmo.microservices.shop.order.api.messaging.OrderSuccessPaidEvent;
import com.itmo.microservices.shop.order.api.service.IOrderService;
import com.itmo.microservices.shop.payment.api.messaging.RefundOrderAnswerEvent;
import com.itmo.microservices.shop.payment.api.messaging.RefundOrderRequestEvent;
import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.api.model.RefundOrderDto;
import com.itmo.microservices.shop.payment.api.service.PaymentService;
import com.itmo.microservices.shop.payment.impl.config.ExternalPaymentServiceCredentials;
import com.itmo.microservices.shop.payment.impl.entity.*;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentFailedException;
import com.itmo.microservices.shop.payment.impl.logging.PaymentServiceNotableEvent;
import com.itmo.microservices.shop.payment.impl.repository.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class DefaultPaymentService implements PaymentService {
    private static final long POLLING_RETRY_INTERVAL_MILLIS = 500;
    private static final int RETRYING_EXECUTOR_POOL_SIZE = 5;
    private static final int REFUND_POOL_SIZE = 20;
    private static final int REFUND_RETRY_INTERVAL_MILLIS = 20000;
    private static final int REFUND_LIFETIME_INTERVAL_MILLIS = 120000;

    private final ExternalServiceClient pollingClient;
    private final ExternalServiceClient syncClient;

    private final IOrderService orderService;

    private final TransactionProcessor<TransactionResponseDto, UUID, TransactionContext> syncTransactionProcessor;
    private final TransactionProcessor<TransactionResponseDto, UUID, TransactionContext> pollingTransactionProcessor;

    private final PaymentLogRecordRepository paymentLogRecordRepo;
    private final FinancialOperationTypeRepository financialOperationTypeRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentTransactionsProcessorWritebackRepository paymentTransactionsProcessorWritebackRepository;
    private final  RefundOrderRepository refundOrderRepository;

    @InjectEventLogger
    private EventLogger eventLogger;

    PriorityBlockingQueue<RefundOrderDto> queue;

    private final EventBus eventBus;

    public DefaultPaymentService(PaymentLogRecordRepository paymentLogRecordRepo,
                                 FinancialOperationTypeRepository financialOperationTypeRepository,
                                 PaymentStatusRepository paymentStatusRepository,
                                 ExternalPaymentServiceCredentials externalPaymentServiceCredentials,
                                 IOrderService orderService, PaymentTransactionsProcessorWritebackRepository paymentTransactionsProcessorWritebackRepository,
                                 RefundOrderRepository refundOrderRepository, EventBus eventBus) {
        this.paymentLogRecordRepo = paymentLogRecordRepo;
        this.financialOperationTypeRepository = financialOperationTypeRepository;
        this.paymentStatusRepository = paymentStatusRepository;
        this.orderService = orderService;
        this.paymentTransactionsProcessorWritebackRepository = paymentTransactionsProcessorWritebackRepository;
        this.queue = new PriorityBlockingQueue<>(REFUND_POOL_SIZE);


        pollingClient = new ExternalServiceClient(externalPaymentServiceCredentials.getUrl(), externalPaymentServiceCredentials.getPollingSecret());
        syncClient = new ExternalServiceClient(externalPaymentServiceCredentials.getUrl(), externalPaymentServiceCredentials.getSyncSecret());
        this.refundOrderRepository = refundOrderRepository;
        this.eventBus = eventBus;

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

    private PaymentSubmissionDto externalRequest(String orderId, String operationType) throws PaymentFailedException {

        PaymentTransactionsProcessorWriteback entry = new PaymentTransactionsProcessorWriteback();

        // TODO move user id setting to the payment controller, get it this method argument
        entry.setUserId(UUID.fromString("E99B7EE6-EE5E-4CB9-9CDD-33FE50765E6E"));
        entry.setFinancialOperationTypeName(operationType);

        UUID orderUUID = UUID.fromString(orderId);

        try {
            entry.setAmount(orderService.getAmount(orderUUID));
            entry.setOrderId(orderUUID);
        } catch (NoSuchElementException e) {
            // TODO collect metrics here
            throw new PaymentFailedException(String.format("Payment failed because of %s", e.getMessage()));
        }

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

    public PaymentSubmissionDto orderPayment(String orderId) throws PaymentFailedException {
        eventLogger.info(PaymentServiceNotableEvent.I_PAYMENT_REQUEST_ADDED_SUCCESSFULLY, orderId);
        return this.externalRequest(orderId, FinancialOperationTypeRepository.VALUES.WITHDRAW.name());
    }

    @Subscribe
    public void handleRefund(RefundOrderRequestEvent refundOrderRequestEvent) {

        if (refundOrderRepository.existsByOrderId(refundOrderRequestEvent.getOrderUUID())) {
            eventLogger.error(PaymentServiceNotableEvent.E_REFUND_ALREADY_REQUESTED, refundOrderRequestEvent.getOrderUUID());
            return;
        }

        if (paymentLogRecordRepo.existsByOrderIdAndPaymentStatusAndFinancialOperationType(
                refundOrderRequestEvent.getOrderUUID()
                , paymentStatusRepository.findByName(PaymentStatusRepository.VALUES.SUCCESS.name()),
                financialOperationTypeRepository.findFinancialOperationTypeByName(FinancialOperationTypeRepository.VALUES.REFUND.name()))
        ) {
            eventBus.post(new RefundOrderAnswerEvent(refundOrderRequestEvent.getOrderUUID(), PaymentStatusRepository.VALUES.FAILED.name()));
            eventLogger.error(PaymentServiceNotableEvent.E_REFUND_ALREADY_DONE, refundOrderRequestEvent.getOrderUUID());
            return;
        }
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setOrderId(refundOrderRequestEvent.getOrderUUID());
        refundOrder.setPrice(refundOrderRequestEvent.getPrice());
        refundOrder.setRequestTime(new Date());
        refundOrderRepository.save(refundOrder);
        eventLogger.info(PaymentServiceNotableEvent.I_REFUND_REQUEST_ADDED_SUCCESSFULLY, refundOrder.getOrderId());
    }


    @Scheduled(fixedDelay = REFUND_RETRY_INTERVAL_MILLIS)
    public void doRefund() {
        updateQueue();
        int queueSize = queue.size();
        for (int i = 0; i < queueSize; i++) {
            RefundOrderDto refundOrderDto = queue.remove();
            try {

                PaymentLogRecord paymentLogRecord = paymentLogRecordRepo.findByTransactionId(refundOrderDto.getTransactionId());

                if (paymentLogRecord == null ||
                        paymentLogRecord.getPaymentStatus().getName().equals(PaymentStatusRepository.VALUES.FAILED.name())
                ) {
                    long requestLifetime = (new Date()).getTime() - refundOrderDto.getRequestTime().getTime();
                    if (requestLifetime > REFUND_LIFETIME_INTERVAL_MILLIS) {
                        removeFromRepo(refundOrderDto, PaymentStatusRepository.VALUES.FAILED.name());
                        eventLogger.error(PaymentServiceNotableEvent.E_REFUND_TIMEOUT, refundOrderDto.getOrderId());
                        continue;
                    }
                    PaymentSubmissionDto paymentSubmissionDto = this.externalRequest(refundOrderDto.getOrderId().toString(),
                            FinancialOperationTypeRepository.VALUES.REFUND.name());

                    RefundOrder refundOrderToUpdate = refundOrderRepository.findByOrderId(refundOrderDto.getOrderId());
                    refundOrderToUpdate.setTransactionId(paymentSubmissionDto.getTransactionId());
                    refundOrderRepository.save(refundOrderToUpdate);
                    continue;
                }

                removeFromRepo(refundOrderDto, PaymentStatusRepository.VALUES.SUCCESS.name());
                eventLogger.info(PaymentServiceNotableEvent.I_REFUND_REQUEST_WAS_SUCCESSFUL, refundOrderDto.getOrderId());
            } catch (Exception e) {
                eventLogger.error(PaymentServiceNotableEvent.E_REFUND_EXCEPTION_SCHEDULER, refundOrderDto.getOrderId());
            }
        }
    }

    private void updateQueue() {
        List<RefundOrder> savedRefunds = refundOrderRepository.findAll();
        queue.clear();
        queue.addAll(savedRefunds.stream().map(RefundOrderDto::toModel).collect(Collectors.toList()));
    }

    private void removeFromRepo(RefundOrderDto refundOrderDto, String status) {
        refundOrderRepository.delete(refundOrderRepository.findByOrderId(refundOrderDto.getOrderId()));
        eventBus.post(new RefundOrderAnswerEvent(refundOrderDto.getOrderId(), status));
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
                paymentLogRecordRepo.saveAndFlush(paymentLogRecord);
                eventBus.post(new OrderSuccessPaidEvent(context.unwrap().getOrderId(), context.unwrap().getUserId(),
                        context.unwrap().getAmount()));
                break;
            case FAILURE:
                paymentLogRecord.setPaymentStatus(paymentStatusRepository.findByName(PaymentStatusRepository.VALUES.FAILED.name()));
                paymentLogRecordRepo.saveAndFlush(paymentLogRecord);
                eventBus.post(new OrderFailedPaidEvent(context.unwrap().getOrderId(), context.unwrap().getUserId(),
                        context.unwrap().getAmount()));
                break;
        }

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
