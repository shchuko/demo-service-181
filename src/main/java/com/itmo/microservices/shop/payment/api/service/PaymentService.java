package com.itmo.microservices.shop.payment.api.service;

import com.itmo.microservices.shop.payment.api.messaging.RefundRequestEvent;
import com.itmo.microservices.shop.payment.api.model.PaymentLogRecordDto;
import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentAlreadyExistsException;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentFailedException;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInUninterruptibleProcessing;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInfoNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    /**
     * Perform a payment. Corresponding payment should be submitted before.
     *
     * @param userId  User ID who pay for the order.
     * @param orderId Order ID of order to be paid.
     * @return Payment submission DTO with payment description.
     * @throws PaymentFailedException If payment has failed.
     */
    @NotNull
    PaymentSubmissionDto payForOrder(@NotNull UUID userId, @NotNull UUID orderId) throws PaymentFailedException, PaymentAlreadyExistsException, PaymentInfoNotFoundException;

    /**
     * Submit payment to be paid by the user. The user must pay in expirationTimeoutMillis,
     * otherwise the payment will be auto-cancelled.
     *
     * @param userId                  User ID.
     * @param orderId                 Order ID.
     * @param expirationTimeoutMillis Timeout the payment will expire in milliseconds.
     */
    void submitPayment(@NotNull UUID userId, @NotNull UUID orderId, int amount, long expirationTimeoutMillis) throws PaymentAlreadyExistsException;

    /**
     * Cancel submitted payment.
     *
     * @param userId  User ID.
     * @param orderId Order ID.
     */
    void cancelPayment(@NotNull UUID userId, @NotNull UUID orderId) throws PaymentInfoNotFoundException, PaymentInUninterruptibleProcessing;

    /**
     * List user financial log.
     *
     * @param userId User ID.
     * @return User financial log records list (empty list of not exists).
     */
    List<UserAccountFinancialLogRecordDto> listUserFinLog(UUID userId);

    /**
     * List user financial log for special order.
     *
     * @param userId  User ID.
     * @param orderId Order ID.
     * @return User financial log records list (empty list of not exists).
     */
    List<UserAccountFinancialLogRecordDto> listUserFinLog(UUID userId, UUID orderId);

    /**
     * List order paument log
     *
     * @param userId  User ID.
     * @param orderId Order ID.
     * @return Order payment log records list (empty list of not exists).
     */
    List<PaymentLogRecordDto> listOrderPaymentLog(UUID userId, UUID orderId);

    /**
     * Refund request handler. Should be overwritten and subscribed to the events.
     *
     * @param event Refund event to handle.
     */
    void handleRefund(@NotNull RefundRequestEvent event) throws PaymentAlreadyExistsException;
}
