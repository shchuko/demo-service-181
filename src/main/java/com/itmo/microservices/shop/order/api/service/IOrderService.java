package com.itmo.microservices.shop.order.api.service;

import com.itmo.microservices.shop.delivery.api.messaging.DeliveryStatusFailedEvent;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryStatusSuccessEvent;
import com.itmo.microservices.shop.order.api.model.BookingDTO;
import com.itmo.microservices.shop.order.api.model.OrderDTO;
import com.itmo.microservices.shop.payment.api.messaging.PaymentCancelledEvent;
import com.itmo.microservices.shop.payment.api.messaging.PaymentFailedEvent;
import com.itmo.microservices.shop.payment.api.messaging.PaymentSuccessfulEvent;

import java.util.UUID;


public interface IOrderService {
    /**
     * Create new order.
     * <p/>Possible order status changes:
     * <ul>
     *     <li>[NONE] -> COLLECTING</li>
     * </ul>
     *
     * @param userId Order creator id.
     * @return Created order description.
     */
    OrderDTO createOrder(UUID userId);

    /**
     * Describe existing order.
     *
     * @param userId  Order creator id.
     * @param orderId Order id.
     * @return Order description.
     */
    OrderDTO describeOrder(UUID userId, UUID orderId);

    /**
     * Add item to exising order.
     * <p/>Possible order status changes:
     * <ul>
     *     <li>COLLECTING -> COLLECTING</li>
     *      <li>BOOKED -> COLLECTING</li>
     * </ul>
     *
     * @param userId  Order creator id.
     * @param orderId Order ID.
     * @param itemId  Item ID.
     * @param amount  Items amount (number of items to add).
     */
    void addItem(UUID userId, UUID orderId, UUID itemId, int amount);

    /**
     * Set time slot to order. Allowed only if order BOOKED.
     * <p/>Possible order status changes:
     * <ul>
     *     <li>BOOKED -> BOOKED</li>
     * </ul>
     *
     * @param userId  Order creator id.
     * @param orderId Order ID.
     * @param time    Time slot time.
     * @return order booking model.
     */
    BookingDTO setTimeSlot(UUID userId, UUID orderId, int time);


    /**
     * Finalize order. Allowed only if order COLLECTING.
     * <p/>Possible order status changes:
     * <ul>
     *     <li>COLLECTING -> BOOKED</li>
     * </ul>
     *
     * @param userId  Order creator id.
     * @param orderId Order ID.
     * @return order booking model.
     */
    BookingDTO finalizeOrder(UUID userId, UUID orderId);


    /**
     * Payment fault handler.
     * <p/>Possible order status changes:
     * <ul>
     *     <li>BOOKED -> BOOKED</li>
     * </ul>
     *
     * @param event Event to handle.
     */
    void handlePaymentFault(PaymentFailedEvent event);


    /**
     * Payment success handler. Starts delivery for WITHGRAW payments.
     * <p/>Possible order status changes:
     * <ul>
     *     <li>BOOKED -> PAID</li>
     *     <li>PAID -> REFUND</li>
     * </ul>
     *
     * @param event Event to handle.
     */
    void handlePaymentSuccess(PaymentSuccessfulEvent event);

    /**
     * Payment cancellation handler.
     * <p/>Possible order status changes:
     * <ul>
     *     <li>BOOKED -> COLLECTING</li>
     * </ul>
     *
     * @param event Event to handle.
     */
    void handlePaymentCancellation(PaymentCancelledEvent event);


    /**
     * Payment fault handler.
     * <p/>Possible order status changes:
     * <ul>
     *     <li>PAID -> REFUND</li>
     * </ul>
     *
     * @param event Event to handle.
     */
    void handleDeliveryFault(DeliveryStatusFailedEvent event);


    /**
     * Payment success handler.
     * <p/><Possible order status changes:
     * <ul>
     *     <li>PAID -> COMPLETED</li>
     * </ul>
     *
     * @param event Event to handle.
     */
    void handleDeliverySuccess(DeliveryStatusSuccessEvent event);

}
