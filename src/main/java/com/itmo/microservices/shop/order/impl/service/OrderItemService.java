package com.itmo.microservices.shop.order.impl.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.commonlib.logging.NotableEvent;
import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException;
import com.itmo.microservices.shop.catalog.api.model.BookingDescriptionDto;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.api.service.ItemService;
import com.itmo.microservices.shop.common.metrics.MetricCollector;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryStatusEvent;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryStatusFailedEvent;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryStatusSuccessEvent;
import com.itmo.microservices.shop.delivery.api.messaging.StartDeliveryEvent;
import com.itmo.microservices.shop.order.api.exeptions.BadOperationForCurrentOrderStatus;
import com.itmo.microservices.shop.order.api.exeptions.OrderNotFoundException;
import com.itmo.microservices.shop.order.api.model.BookingDTO;
import com.itmo.microservices.shop.order.api.model.OrderDTO;
import com.itmo.microservices.shop.order.api.model.PaymentLogRecord;
import com.itmo.microservices.shop.order.api.service.IOrderService;
import com.itmo.microservices.shop.order.impl.entity.OrderItem;
import com.itmo.microservices.shop.order.impl.entity.OrderItemID;
import com.itmo.microservices.shop.order.impl.entity.OrderStatus;
import com.itmo.microservices.shop.order.impl.entity.OrderTable;
import com.itmo.microservices.shop.order.impl.logging.OrderServiceNotableEvent;
import com.itmo.microservices.shop.order.impl.mapper.OrderTableToOrderDTO;
import com.itmo.microservices.shop.order.impl.metrics.OrderMetricEvent;
import com.itmo.microservices.shop.order.impl.repository.IOrderItemRepository;
import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;
import com.itmo.microservices.shop.order.impl.repository.IOrderTableRepository;
import com.itmo.microservices.shop.order.messaging.OrderCreatedEvent;
import com.itmo.microservices.shop.payment.api.messaging.*;
import com.itmo.microservices.shop.payment.api.service.PaymentService;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentAlreadyExistsException;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInUninterruptibleProcessing;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInfoNotFoundException;
import com.itmo.microservices.shop.payment.impl.repository.FinancialOperationTypeRepository;
import com.itmo.microservices.shop.user.api.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("UnstableApiUsage")
public class OrderItemService implements IOrderService {
    // TODO: add private method for changing order status and (logging + metrics)
    private static final long BOOKING_TIMEOUT_MILLIS = 1000 * 60 * 5; // 5 minutes to pay for the order
    private static final String SUCCESSFUL_FINALIZATION = "SUCCESS";
    private static final String FAILED_FINALIZATION = "FAILED";

    private final UserService userService;
    private final IOrderItemRepository orderItemRepository;
    private final IOrderStatusRepository statusRepository;
    private final IOrderTableRepository orderRepository;

    @InjectEventLogger
    private EventLogger eventLogger;
    private final PaymentService paymentService;
    private final EventBus eventBus;
    private final ItemService itemService;
    private final MetricCollector metricCollector;

    public OrderItemService(UserService userService,
                            IOrderItemRepository orderItemRepository,
                            IOrderStatusRepository statusRepository,
                            IOrderTableRepository orderRepository,
                            PaymentService paymentService,
                            EventBus eventBus,
                            ItemService itemService,
                            MetricCollector metricCollector) {
        this.userService = userService;
        this.orderItemRepository = orderItemRepository;
        this.statusRepository = statusRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.eventBus = eventBus;
        this.itemService = itemService;
        this.metricCollector = metricCollector;
        metricCollector.register(OrderMetricEvent.values());
    }

    @Override
    public OrderDTO createOrder(UUID userId) {
        OrderStatus statusCollecting = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COLLECTING.name());
        OrderTable order = new OrderTable();
        order.setTimeCreated(Instant.now().getEpochSecond());
        order.setStatus(statusCollecting);
        order.setUserId(userId);
        orderRepository.save(order);
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_IN_STATUS,
                1,
                IOrderStatusRepository.StatusNames.COLLECTING.name()
        );
        //noinspection unchecked
        OrderDTO orderDTO = OrderTableToOrderDTO.toDTO(order, Collections.EMPTY_LIST);
        eventBus.post(new OrderCreatedEvent(orderDTO));
        logInfo(OrderServiceNotableEvent.I_ORDER_CREATED, order.getId());
        metricCollector.passEvent(OrderMetricEvent.ORDER_CREATED, 1);
        return orderDTO;
    }

    @Override
    public OrderDTO describeOrder(UUID userId, UUID orderId) {
        return OrderTableToOrderDTO.toDTO(
                getOrderOrThrow(orderId, userId),
                paymentService.listOrderPaymentLog(userId, orderId).stream()
                        .map(PaymentLogRecord::new)
                        .collect(Collectors.toList()));
    }

    @Override
    public void addItem(UUID userId, UUID orderId, UUID itemId, int amount) {
        /* COLLECTING -> COLLECTING */
        /* BOOKED -> COLLECTING */
        OrderTable order = getOrderOrThrow(orderId, userId);
        var status = IOrderStatusRepository.StatusNames.valueOf(order.getStatus().getName());
        switch (status) {
            case COLLECTING:
                /* Already in COLLECTING state, nothing to do */
                break;

            case BOOKED:
                /* Must be un-booked and made COLLECTING */
                try {
                    paymentService.cancelPayment(userId, orderId);
                } catch (PaymentInfoNotFoundException e) {
                    throw new RuntimeException("PaymentInfoNotFoundException: Should not be reached", e);
                } catch (PaymentInUninterruptibleProcessing e) {
                    /* Cannot cancel payment - already paid, transaction in progress */
                    logError(OrderServiceNotableEvent.E_CONFLICT, "'AddItem' cannot be performed because of payment processing, order=" + orderId);
                    throw new BadOperationForCurrentOrderStatus("'AddItem' cannot be performed because of payment processing", orderId);
                }

                order.setStatus(statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COLLECTING.name()));
                itemService.cancelBooking(order.getLastBookingId());
                orderRepository.save(order);
                logInfo(OrderServiceNotableEvent.I_ORDER_UNBOOKED, orderId);
                metricCollector.passEvent(
                        OrderMetricEvent.ORDER_STATUS_CHANGED,
                        1,
                        IOrderStatusRepository.StatusNames.BOOKED.name(),
                        IOrderStatusRepository.StatusNames.COLLECTING.name()
                        );
                metricCollector.passEvent(
                        OrderMetricEvent.ORDER_IN_STATUS,
                        1,
                        IOrderStatusRepository.StatusNames.COLLECTING.name()
                );
                metricCollector.passEvent(
                        OrderMetricEvent.ORDER_IN_STATUS,
                        -1,
                        IOrderStatusRepository.StatusNames.BOOKED.name()
                );
                break;

            default:
                logError(OrderServiceNotableEvent.E_CONFLICT, "'AddItem' allowed only for 'BOOKED' or 'COLLECTING' order=" + orderId + " status=" + status);
                throw new BadOperationForCurrentOrderStatus("'AddItem' allowed only for 'BOOKED' or 'COLLECTING' order", orderId, status);
        }

        ItemDTO item;
        try {
            item = itemService.describeItem(itemId);
        } catch (ItemNotFoundException exception) {
            logError(OrderServiceNotableEvent.E_ITEM_NOT_FOUND, itemId);
            throw exception;
        }

        Optional<OrderItem> maybeOrderItem = orderItemRepository.findById(new OrderItemID(orderId, itemId));
        OrderItem orderItem = maybeOrderItem.orElseGet(OrderItem::new);
        orderItem.setOrderId(orderId);
        orderItem.setPrice(item.getPrice());
        orderItem.setOrder(order);
        orderItem.setAmount(amount);
        orderItem.setItemId(itemId);

        orderItemRepository.save(orderItem);
        logInfo(OrderServiceNotableEvent.I_ITEM_ADDED, "order=" + orderId + " itemId=" + itemId);
        metricCollector.passEvent(OrderMetricEvent.ITEM_ADDED, 1);
    }

    @Override
    public BookingDTO setTimeSlot(UUID userId, UUID orderId, int time) {
        /* BOOKED -> BOOKED */
        metricCollector.passEvent(
                OrderMetricEvent.TIMESLOT_SET_REQUEST_COUNT,
                1
        );
        var order = getOrderOrThrow(orderId, userId);

        var status = IOrderStatusRepository.StatusNames.valueOf(order.getStatus().getName());
        //noinspection SwitchStatementWithTooFewBranches
        switch (status) {
            //case COLLECTING: /* TODO should we allow set time slot while COLLECTING? The behaviour must be concreted */
            case BOOKED:
                break;
            default:
                logError(OrderServiceNotableEvent.E_CONFLICT, "'SetTimeSlot' allowed only for 'BOOKED', got order=" + orderId + " status=" + status);
                throw new BadOperationForCurrentOrderStatus("'SetTimeSlot' allowed only for 'BOOKED' order", orderId, status);
        }

        order.setDeliveryDuration(time);
        orderRepository.save(order);

        logInfo(OrderServiceNotableEvent.I_ORDER_SET_TIME_SLOT, "order=" + orderId + " slot=" + time);
        var bookingDescription = itemService.describeBooking(order.getLastBookingId());
        return new BookingDTO(bookingDescription.getBookingId(), bookingDescription.getFailedItems().keySet());
    }

    @Override
    public BookingDTO finalizeOrder(UUID userId, UUID orderId) {
        /* COLLECTING -> BOOKED */
        metricCollector.passEvent(
                OrderMetricEvent.ADD_TO_FINALIZED_ORDER_REQUEST,
                1
        );
        long start = System.currentTimeMillis();
        OrderTable order = getOrderOrThrow(orderId, userId);
        var collectingStatus = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COLLECTING.name());

        if (!order.getStatus().equals(collectingStatus)) {
            var status = IOrderStatusRepository.StatusNames.valueOf(order.getStatus().getName());
            logError(OrderServiceNotableEvent.E_CONFLICT, "'FinalizeOrder' allowed only for 'COLLECTING', got order=" + orderId + " status=" + status);
            metricCollector.passEvent(
                    OrderMetricEvent.FINALIZATION_ATTEMPT,
                    1,
                    FAILED_FINALIZATION
            );
            throw new BadOperationForCurrentOrderStatus("'FinalizeOrder' allowed only for 'COLLECTING' order", orderId, status);
        }

        var statusBooked = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.BOOKED.name());
        var items = order.getOrderItems().stream().collect(Collectors.toMap(OrderItem::getItemId, OrderItem::getAmount));

        BookingDescriptionDto bookingResult;
        try {
            bookingResult = itemService.book(items);
        } catch (ItemNotFoundException e) {
            logError(OrderServiceNotableEvent.E_ITEM_NOT_FOUND, e.getMessage());
            metricCollector.passEvent(
                    OrderMetricEvent.FINALIZATION_ATTEMPT,
                    1,
                    FAILED_FINALIZATION
            );
            throw e;
        }
        order.setStatus(statusBooked);
        order.setLastBookingId(bookingResult.getBookingId());
        orderRepository.save(order);

        metricCollector.passEvent(
                OrderMetricEvent.ORDER_IN_STATUS,
                -1,
                IOrderStatusRepository.StatusNames.COLLECTING.name()
        );
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_IN_STATUS,
                1,
                IOrderStatusRepository.StatusNames.BOOKED.name()
        );
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_STATUS_CHANGED,
                1,
                IOrderStatusRepository.StatusNames.COLLECTING.name(),
                IOrderStatusRepository.StatusNames.BOOKED.name()
        );
        logInfo(OrderServiceNotableEvent.I_ORDER_FINALIZED, orderId);

        try {
            /* TODO retrieve adekvatny amount from item service while booking */
            var amount = 42;
            paymentService.submitPayment(userId, orderId, amount, BOOKING_TIMEOUT_MILLIS);
            logInfo(OrderServiceNotableEvent.I_ORDER_SUBMIT_PAYMENT, orderId);
        } catch (PaymentAlreadyExistsException e) {
            logError(OrderServiceNotableEvent.E_ORDER_SUBMIT_PAYMENT, orderId);
            metricCollector.passEvent(
                    OrderMetricEvent.FINALIZATION_ATTEMPT,
                    1,
                    FAILED_FINALIZATION
            );
            throw new RuntimeException("Should not be reached", e);
        }
        metricCollector.passEvent(
                OrderMetricEvent.FINALIZATION_DURATION,
                (double) (System.currentTimeMillis() - start));
        metricCollector.passEvent(
                OrderMetricEvent.FINALIZATION_ATTEMPT,
                1,
                SUCCESSFUL_FINALIZATION
        );
        return new BookingDTO(bookingResult.getBookingId(), bookingResult.getFailedItems().keySet());
    }

    @Subscribe
    @Override
    public void handlePaymentSuccess(PaymentSuccessfulEvent event) {
        /* BOOKED -> PAID */
        /* PAID -> REFUND */
        OrderTable order = getOrderOrThrow(event.getOrderId(), event.getUserId());

        switch (FinancialOperationTypeRepository.VALUES.valueOf(event.getOperationType())) {
            case REFUND:
//                TODO uncomment, now we calling onRefundComplete triggerRefund
//                onRefundComplete(order);
                metricCollector.passEvent(
                        OrderMetricEvent.ORDER_STATUS_CHANGED,
                        1,
                        IOrderStatusRepository.StatusNames.PAID.name(),
                        IOrderStatusRepository.StatusNames.REFUND.name()
                );
                metricCollector.passEvent(
                        OrderMetricEvent.ORDER_IN_STATUS,
                        1,
                        IOrderStatusRepository.StatusNames.REFUND.name()
                );
                metricCollector.passEvent(
                        OrderMetricEvent.ORDER_IN_STATUS,
                        -1,
                        IOrderStatusRepository.StatusNames.PAID.name()
                );
                break;
            case WITHDRAW:
                OrderStatus statusPaid = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.PAID.name());
                order.setStatus(statusPaid);
                orderRepository.save(order);
                metricCollector.passEvent(
                        OrderMetricEvent.ORDER_STATUS_CHANGED,
                        1,
                        IOrderStatusRepository.StatusNames.BOOKED.name(),
                        IOrderStatusRepository.StatusNames.PAID.name()
                );
                metricCollector.passEvent(
                        OrderMetricEvent.ORDER_IN_STATUS,
                        1,
                        IOrderStatusRepository.StatusNames.PAID.name()
                );
                metricCollector.passEvent(
                        OrderMetricEvent.ORDER_IN_STATUS,
                        -1,
                        IOrderStatusRepository.StatusNames.BOOKED.name()
                );
                itemService.completeBooking(order.getLastBookingId());
                logInfo(OrderServiceNotableEvent.I_ORDER_SUCCESSFUL_PAYMENT, order.getId());
                if (order.getDeliveryDuration() == null) {
                    triggerRefund(order);
                } else {
                    eventBus.post(new StartDeliveryEvent(order.getId(), order.getUserId(), order.getDeliveryDuration()));
                }
                break;
        }
    }

    @Subscribe
    @Override
    public void handlePaymentFault(PaymentFailedEvent event) {
        /* BOOKED -> BOOKED */
        /* PAID -> [trigger refund] */
        OrderTable order = getOrderOrThrow(event.getOrderId(), event.getUserId());

        switch (FinancialOperationTypeRepository.VALUES.valueOf(event.getOperationType())) {
            case REFUND:
                /* Retry refund */
//                TODO uncomment, now we calling onRefundComplete triggerRefund
//                triggerRefund(order);
                break;
            case WITHDRAW:
                /* Ignore and let handlePaymentCancellation() do everything */
                logInfo(OrderServiceNotableEvent.I_ORDER_FAILED_PAYMENT, order.getId());
                break;
        }
    }

    @Subscribe
    @Override
    public void handlePaymentCancellation(PaymentCancelledEvent event) {
        /* BOOKED -> COLLECTING */
        OrderTable order = getOrderOrThrow(event.getOrderId(), event.getUserId());
        if (FinancialOperationTypeRepository.VALUES.REFUND.name().equals(event.getOperationType())) {
            /* Ignored for now */
            return;
        }

        itemService.cancelBooking(order.getLastBookingId());
        OrderStatus statusCollecting = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COLLECTING.name());
        order.setStatus(statusCollecting);
        orderRepository.save(order);
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_STATUS_CHANGED,
                1,
                IOrderStatusRepository.StatusNames.BOOKED.name(),
                IOrderStatusRepository.StatusNames.COLLECTING.name()
        );
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_IN_STATUS,
                1,
                IOrderStatusRepository.StatusNames.COLLECTING.name()
        );
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_IN_STATUS,
                -1,
                IOrderStatusRepository.StatusNames.BOOKED.name()
        );
        logInfo(OrderServiceNotableEvent.I_ORDER_CANCELLED_PAYMENT, order.getId());
    }

    @Subscribe
    @Override
    public void handleDeliverySuccess(DeliveryStatusSuccessEvent event) {
        /* PAID -> COMPLETED */
        OrderTable order = deliveryEventCommonPreHandler(event);
        OrderStatus statusComplete = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COMPLETED.name());
        order.setStatus(statusComplete);
//        order.setDeliveryDuration(event.getDeliveryDuration());
        orderRepository.save(order);
        logInfo(OrderServiceNotableEvent.I_ORDER_SUCCESSFUL_DELIVERY, event.getOrderId());
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_STATUS_CHANGED,
                1,
                IOrderStatusRepository.StatusNames.PAID.name(),
                IOrderStatusRepository.StatusNames.COMPLETED.name()
        );
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_IN_STATUS,
                1,
                IOrderStatusRepository.StatusNames.COMPLETED.name()
        );
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_IN_STATUS,
                -1,
                IOrderStatusRepository.StatusNames.PAID.name()
        );
    }

    @Subscribe
    @Override
    public void handleDeliveryFault(DeliveryStatusFailedEvent event) {
        /* PAID -> REFUND */
        triggerRefund(deliveryEventCommonPreHandler(event));
        logInfo(OrderServiceNotableEvent.I_ORDER_FAILED_DELIVERY, event.getOrderId());
    }

    private void triggerRefund(OrderTable order) {
        /* TODO retrieve adekvatny amount from item service while booking */
        var amount = 42;
        eventBus.post(new RefundRequestEvent(order.getId(), order.getUserId(), amount));
        logInfo(OrderServiceNotableEvent.I_REFUND_STARTED, order.getId());

        /* TODO remove onRefundComplete, it should be called on transaction finish! */
        onRefundComplete(order);
    }

    private void onRefundComplete(OrderTable order) {
        /* <ANY> -> REFUND */
        OrderStatus currentStatus = order.getStatus();
        itemService.processRefund(order.getLastBookingId());
        OrderStatus statusRefund = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.REFUND.name());
        order.setStatus(statusRefund);
        orderRepository.save(order);
        logInfo(OrderServiceNotableEvent.I_REFUND_DONE, order.getId());
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_STATUS_CHANGED,
                1,
                currentStatus.getName(),
                IOrderStatusRepository.StatusNames.REFUND.name()
        );
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_IN_STATUS,
                1,
                IOrderStatusRepository.StatusNames.REFUND.name()
        );
        metricCollector.passEvent(
                OrderMetricEvent.ORDER_IN_STATUS,
                -1,
                currentStatus.getName()
        );
    }

    private OrderTable deliveryEventCommonPreHandler(DeliveryStatusEvent event) {
        OrderTable order = getOrderOrThrow(event.getOrderId(), event.getUserId());
        switch (IOrderStatusRepository.StatusNames.valueOf(order.getStatus().getName())) {
            case PAID:
            case SHIPPING:
                break;
            default:
                logError(OrderServiceNotableEvent.E_ILLEGAL_STATE, "Delivery status handler for order in state" + order.getStatus().getName());
                throw new IllegalStateException();
        }
        return order;
    }

    private OrderTable getOrderOrThrow(UUID orderId, UUID userId) {
        Optional<OrderTable> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            logError(OrderServiceNotableEvent.E_NO_SUCH_ORDER, orderId);
            throw new OrderNotFoundException("No order with id=" + orderId);
        }

        /* Basic user can access only theirs orders, admin can access any order */
        if (!orderOptional.get().getUserId().equals(userId) && !userService.getUserByID(userId).isAdmin()) {
            logError(OrderServiceNotableEvent.E_NOT_ALLOWED, orderId);
            throw new OrderNotFoundException("No order with id=" + orderId);
        }
        return orderOptional.get();
    }

    private void logInfo(@NotNull NotableEvent event, Object... argv) {
        if (eventLogger != null) {
            eventLogger.info(event, argv);
        }
    }

    private void logError(@NotNull NotableEvent event, Object... argv) {
        if (eventLogger != null) {
            eventLogger.error(event, argv);
        }
    }
}
