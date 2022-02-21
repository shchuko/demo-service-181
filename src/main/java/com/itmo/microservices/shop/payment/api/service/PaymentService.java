package com.itmo.microservices.shop.payment.api.service;

import com.itmo.microservices.shop.payment.api.messaging.RefundRequestEvent;
import com.itmo.microservices.shop.payment.api.model.PaymentSubmissionDto;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentAlreadyExistsException;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentFailedException;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInUninterruptibleProcessing;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInfoNotFoundException;
import org.jetbrains.annotations.NotNull;

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
     * Refund request handler. Should be overwritten and subscribed to the events.
     *
     * @param event Refund event to handle.
     */
    void handleRefund(@NotNull RefundRequestEvent event) throws PaymentAlreadyExistsException;
}
