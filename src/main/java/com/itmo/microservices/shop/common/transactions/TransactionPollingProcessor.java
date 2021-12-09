package com.itmo.microservices.shop.common.transactions;

import com.itmo.microservices.shop.common.executors.RetryingExecutorService;
import com.itmo.microservices.shop.common.executors.exceptions.StopRetrying;
import com.itmo.microservices.shop.common.limiters.Limiter;
import com.itmo.microservices.shop.common.transactions.exception.StopPolling;
import com.itmo.microservices.shop.common.transactions.exception.TransactionStartException;
import com.itmo.microservices.shop.common.transactions.functional.ExceptionHandler;
import com.itmo.microservices.shop.common.transactions.functional.TransactionProcessor;
import com.itmo.microservices.shop.common.transactions.functional.TransactionStarter;
import com.itmo.microservices.shop.common.transactions.functional.TransactionUpdater;

import java.util.*;
import java.util.function.BiConsumer;

public class TransactionPollingProcessor<T, ID, C> implements TransactionProcessor<T, ID, C> {
    private final RetryingExecutorService retryingExecutorService;
    private final WriteBackStorage<ID, C> writeBackStorage;
    private final TransactionStarter<T, ID> transactionStarter;
    private final TransactionUpdater<T, ID> transactionUpdater;
    private final BiConsumer<TransactionWrapper<T, ID>, C> transactionFinishHandler;
    private final Map<Class<? extends Throwable>, ExceptionHandler> exceptionHandlers;
    private final List<Limiter> limiterList;

    public static <T, ID, C> Builder<T, ID, C> builder() {
        return new Builder<>();
    }

    public static class Builder<T, ID, C> {
        /* Required fields */
        private RetryingExecutorService retryingExecutorService;
        private TransactionStarter<T, ID> transactionStarter;
        private TransactionUpdater<T, ID> transactionUpdater;

        /* Optional fields */
        private WriteBackStorage<ID, C> writeBackStorage;
        private BiConsumer<TransactionWrapper<T, ID>, C> transactionFinishHandler;
        private final List<Limiter> limiterList;
        private final Map<Class<? extends Throwable>, ExceptionHandler> exceptionHandlers = new HashMap<>();

        {
            /* Ignoring stubs */
            transactionFinishHandler = (var wrapper, var context) -> {
            };
            limiterList = new ArrayList<>();
            writeBackStorage = new WriteBackStorage<>() {
                @Override
                public void add(ID id, C context) {

                }

                @Override
                public void remove(ID id) {

                }

                @Override
                public List<Map.Entry<ID, C>> getAll() {
                    return Collections.emptyList();
                }
            };
        }

        public TransactionProcessor<T, ID, C> build() throws IllegalArgumentException {
            return new TransactionPollingProcessor<>(retryingExecutorService,
                    transactionStarter,
                    transactionUpdater,
                    writeBackStorage,
                    transactionFinishHandler,
                    exceptionHandlers, limiterList);
        }

        public Builder<T, ID, C> withPollingExecutorService(RetryingExecutorService service) {
            this.retryingExecutorService = service;
            return this;
        }

        public Builder<T, ID, C> withTransactionStartLimiter(Limiter limiter) {
            this.limiterList.add(limiter);
            return this;
        }

        public Builder<T, ID, C> withWriteBackStorage(WriteBackStorage<ID, C> writeBackStorage) {
            this.writeBackStorage = writeBackStorage;
            return this;
        }

        public Builder<T, ID, C> withTransactionStarter(TransactionStarter<T, ID> transactionStarter) {
            this.transactionStarter = transactionStarter;
            return this;
        }

        public Builder<T, ID, C> withTransactionUpdater(TransactionUpdater<T, ID> transactionUpdater) {
            this.transactionUpdater = transactionUpdater;
            return this;
        }

        public Builder<T, ID, C> withTransactionFinishHandler(BiConsumer<TransactionWrapper<T, ID>, C> transactionFinishHandler) {
            this.transactionFinishHandler = transactionFinishHandler;
            return this;
        }

        public <E extends Throwable> Builder<T, ID, C> withExceptionHandler(Class<E> exceptionClazz, ExceptionHandler handler) {
            this.exceptionHandlers.put(exceptionClazz, handler);
            return this;
        }

        public <E extends Throwable> Builder<T, ID, C> withIgnoringExceptionHandler(Class<E> exceptionClazz) {
            this.exceptionHandlers.put(exceptionClazz, (Throwable exception) -> {
                /* Just ignore the exception and keep retrying */
            });
            return this;
        }

        public <E extends Throwable> Builder<T, ID, C> withTerminatingExceptionHandler(Class<E> exceptionClazz) {
            this.exceptionHandlers.put(exceptionClazz, (Throwable exception) -> {
                throw new StopPolling(exception);
            });
            return this;
        }

        private void validateFieldsOrThrow() {
            if (retryingExecutorService == null) {
                throw new IllegalArgumentException("Polling executor service is not set");
            }
            if (transactionStarter == null) {
                throw new IllegalArgumentException("Transaction Starter is not set");
            }
            if (transactionUpdater == null) {
                throw new IllegalArgumentException("Transaction Updater is not set");
            }
        }

    }

    @Override
    public TransactionWrapper<T, ID> startTransaction(C context, Object... args) throws TransactionStartException {
        if (this.limiterList.stream().allMatch(Limiter::tryAcquire)) {
            TransactionWrapper<T, ID> transactionWrapper = transactionStarter.start(args);
            startPolling(transactionWrapper.getId(), context);
            return transactionWrapper;
        }

        throw new TransactionStartException("Terminated on limiter restriction");
    }

    private void startPolling(ID transactionId, C context) {
        writeBackStorage.add(transactionId, context);

        retryingExecutorService.submitTask(() -> {
            TransactionWrapper<T, ID> transaction;
            try {
                transaction = transactionUpdater.update(transactionId);
            } catch (Throwable e) {
                var handler = exceptionHandlers.getOrDefault(e.getClass(), (Throwable ex) -> {
                    throw new StopPolling(ex);
                });

                try {
                    handler.handle(e);
                } catch (StopPolling stopPollingException) {
                    throw new StopRetrying(stopPollingException);
                }
                return;
            }

            if (TransactionStatus.PENDING.equals(transaction.getStatus())) {
                return;
            }

            writeBackStorage.remove(transaction.getId());
            transactionFinishHandler.accept(transaction, context);
            throw new StopRetrying("Polling for transaction.id=" + transaction.getId() + " finished");
        });
    }


    private TransactionPollingProcessor(RetryingExecutorService retryingExecutorService,
                                        TransactionStarter<T, ID> transactionStarter,
                                        TransactionUpdater<T, ID> transactionUpdater,
                                        WriteBackStorage<ID, C> writeBackStorage,
                                        BiConsumer<TransactionWrapper<T, ID>, C> transactionFinishHandler,
                                        Map<Class<? extends Throwable>, ExceptionHandler> exceptionHandlers, List<Limiter> limiterList) {
        this.retryingExecutorService = retryingExecutorService;
        this.writeBackStorage = writeBackStorage;
        this.transactionStarter = transactionStarter;
        this.transactionUpdater = transactionUpdater;
        this.transactionFinishHandler = transactionFinishHandler;
        this.exceptionHandlers = exceptionHandlers;
        this.limiterList = limiterList;
        writeBackStorage.getAll().forEach(it -> startPolling(it.getKey(), it.getValue()));
    }


}
