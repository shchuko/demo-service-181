package com.itmo.microservices.shop.order.impl.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException;
import com.itmo.microservices.shop.catalog.api.model.BookingDescriptionDto;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.api.service.ItemService;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryStatusEvent;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryStatusFailedEvent;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryStatusSuccessEvent;
import com.itmo.microservices.shop.delivery.api.messaging.StartDeliveryEvent;
import com.itmo.microservices.shop.order.api.exeptions.BadOperationForCurrentOrderStatus;
import com.itmo.microservices.shop.order.api.exeptions.OrderNotFoundException;
import com.itmo.microservices.shop.order.api.model.BookingDTO;
import com.itmo.microservices.shop.order.api.model.OrderDTO;
import com.itmo.microservices.shop.order.api.service.IOrderService;
import com.itmo.microservices.shop.order.impl.entity.OrderItem;
import com.itmo.microservices.shop.order.impl.entity.OrderItemID;
import com.itmo.microservices.shop.order.impl.entity.OrderStatus;
import com.itmo.microservices.shop.order.impl.entity.OrderTable;
import com.itmo.microservices.shop.order.impl.logging.OrderServiceNotableEvent;
import com.itmo.microservices.shop.order.impl.mapper.OrderTableToOrderDTO;
import com.itmo.microservices.shop.order.impl.repository.IOrderItemRepository;
import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;
import com.itmo.microservices.shop.order.impl.repository.IOrderTableRepository;
import com.itmo.microservices.shop.order.messaging.OrderCreatedEvent;
import com.itmo.microservices.shop.payment.api.messaging.*;
import com.itmo.microservices.shop.payment.api.service.PaymentService;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentAlreadyExistsException;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInUninterruptibleProcessing;
import com.itmo.microservices.shop.payment.impl.exceptions.PaymentInfoNotFoundException;
import com.itmo.microservices.shop.user.api.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("UnstableApiUsage")
public class OrderItemService implements IOrderService {
    private static final long BOOKING_TIMEOUT_MILLIS = 1000 * 60;

    private final UserService userService;
    private final IOrderItemRepository orderItemRepository;
    private final IOrderStatusRepository statusRepository;
    private final IOrderTableRepository orderRepository;

    @InjectEventLogger
    private EventLogger eventLogger;
    private final PaymentService paymentService;
    private final EventBus eventBus;
    private final ItemService itemService;

    public OrderItemService(UserService userService,
                            IOrderItemRepository orderItemRepository,
                            IOrderStatusRepository statusRepository,
                            IOrderTableRepository orderRepository,
                            PaymentService paymentService,
                            EventBus eventBus, ItemService itemService) {
        this.userService = userService;
        this.orderItemRepository = orderItemRepository;
        this.statusRepository = statusRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.eventBus = eventBus;
        this.itemService = itemService;
    }

    @Override
    public OrderDTO createOrder(UUID userId) {
        OrderStatus statusCollecting = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COLLECTING.name());
        OrderTable order = new OrderTable();
        order.setTimeCreated(Instant.now().getEpochSecond());
        order.setStatus(statusCollecting);
        order.setUserId(userId);
        orderRepository.save(order);

        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ORDER_CREATED, order.getId());
        }

        OrderDTO orderDTO = OrderTableToOrderDTO.toDTO(order);
        eventBus.post(new OrderCreatedEvent(orderDTO));
        return orderDTO;
    }

    @Override
    public OrderDTO describeOrder(UUID userId, UUID orderId) {
        return OrderTableToOrderDTO.toDTO(getOrderOrThrow(orderId, userId));
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
                    throw new BadOperationForCurrentOrderStatus("'AddItem' cannot be performed because of payment processing", orderId);
                }

                order.setStatus(statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COLLECTING.name()));
                itemService.cancelBooking(order.getLastBookingId());
                orderRepository.save(order);
                break;

            default:
                throw new BadOperationForCurrentOrderStatus("'AddItem' allowed only for 'BOOKED' or 'COLLECTING' order", orderId, status);
        }

        ItemDTO item;
        try {
            item = itemService.describeItem(itemId);
        } catch (ItemNotFoundException exception) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_ITEM_NOT_FOUND, exception.getMessage());
            }
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
        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ITEM_ADDED, itemId);
        }
    }

    @Override
    public BookingDTO setTimeSlot(UUID userId, UUID orderId, int time) {
        /* BOOKED -> BOOKED */
        var order = getOrderOrThrow(orderId, userId);

        var status = IOrderStatusRepository.StatusNames.valueOf(order.getStatus().getName());
        //noinspection SwitchStatementWithTooFewBranches
        switch (status) {
            //case COLLECTING: /* TODO should we allow set time slot while COLLECTING? The behaviour must be concreted */
            case BOOKED:
                break;
            default:
                throw new BadOperationForCurrentOrderStatus("'SetTimeSlot' allowed only for 'BOOKED' order", orderId, status);
        }

        order.setDeliverySlot(time);
        orderRepository.save(order);

        var bookingDescription = itemService.describeBooking(order.getLastBookingId());

        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ORDER_SET_TIME, orderId);
        }
        return new BookingDTO(bookingDescription.getBookingId(), bookingDescription.getFailedItems().keySet());
    }

    @Override
    public BookingDTO finalizeOrder(UUID userId, UUID orderId) {
        /* COLLECTING -> BOOKED */
        OrderTable order = getOrderOrThrow(orderId, userId);
        var collectingStatus = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COLLECTING.name());

        if (!order.getStatus().equals(collectingStatus)) {
            var status = IOrderStatusRepository.StatusNames.valueOf(order.getStatus().getName());
            throw new BadOperationForCurrentOrderStatus("'FinalizeOrder' allowed only for 'COLLECTING' order", orderId, status);
        }

        var statusBooked = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.BOOKED.name());
        var items = order.getOrderItems().stream().collect(Collectors.toMap(OrderItem::getItemId, OrderItem::getAmount));

        BookingDescriptionDto bookingResult;
        try {
            bookingResult = itemService.book(items);
        } catch (ItemNotFoundException e) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_ITEM_NOT_FOUND, e.getMessage());
            }
            throw e;
        }
        order.setStatus(statusBooked);
        order.setLastBookingId(bookingResult.getBookingId());
        orderRepository.save(order);

        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ORDER_BOOKED, orderId);
        }

        try {
            /* TODO retrieve adekvatny amount from item service while booking */
            var amount = 42;
            paymentService.submitPayment(userId, orderId, amount, BOOKING_TIMEOUT_MILLIS);
        } catch (PaymentAlreadyExistsException e) {
            throw new RuntimeException("Should not be reached", e);
        }
        return new BookingDTO(bookingResult.getBookingId(), bookingResult.getFailedItems().keySet());
    }

    @Subscribe
    @Override
    public void handlePaymentSuccess(PaymentSuccessfulEvent event) {
        /* BOOKED -> PAID */
        /* PAID -> REFUND */
        OrderTable order = paymentEventCommonPreHandler(event);

        switch (event.getOperationType()) {
            case REFUND:
//                TODO uncomment, now we calling onRefundComplete triggerRefund
//                onRefundComplete(order);
                break;
            case WITHDRAW:
                OrderStatus statusPaid = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.PAID.name());
                order.setStatus(statusPaid);
                orderRepository.save(order);
                itemService.completeBooking(order.getLastBookingId());
                if (order.getDeliverySlot() == null) {
                    triggerRefund(order);
                } else {
                    eventBus.post(new StartDeliveryEvent(order.getId(), order.getUserId(), order.getDeliverySlot()));
                }
                break;
        }
    }

    @Subscribe
    @Override
    public void handlePaymentFault(PaymentFailedEvent event) {
        /* BOOKED -> BOOKED */
        /* PAID -> [trigger refund] */
        OrderTable order = paymentEventCommonPreHandler(event);

        switch (event.getOperationType()) {
            case REFUND:
                /* Retry refund */
//                TODO uncomment, now we calling onRefundComplete triggerRefund
//                triggerRefund(order);
                break;
            case WITHDRAW:
                /* Ignore and let handlePaymentCancellation() do everything */
                break;
        }
    }

    @Subscribe
    @Override
    public void handlePaymentCancellation(PaymentCancelledEvent event) {
        /* BOOKED -> COLLECTING */
        OrderTable order = paymentEventCommonPreHandler(event);
        itemService.cancelBooking(order.getLastBookingId());
        OrderStatus statusCollecting = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COLLECTING.name());
        order.setStatus(statusCollecting);
        orderRepository.save(order);
    }

    @Subscribe
    @Override
    public void handleDeliverySuccess(DeliveryStatusSuccessEvent event) {
        /* PAID -> COMPLETED */
        OrderTable order = deliveryEventCommonPreHandler(event);
        OrderStatus statusComplete = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COMPLETED.name());
        order.setStatus(statusComplete);
        order.setDeliveryDuration(event.getDeliveryDuration());
        orderRepository.save(order);
        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ORDER_END_DELIVERY, event.getOrderId());
        }
    }

    @Subscribe
    @Override
    public void handleDeliveryFault(DeliveryStatusFailedEvent event) {
        /* PAID -> REFUND */
        triggerRefund(deliveryEventCommonPreHandler(event));
        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ORDER_FAILED_DELIVERY, event.getOrderId());
        }
    }

    private void triggerRefund(OrderTable order) {
        /* TODO retrieve adekvatny amount from item service while booking */
        var amount = 42;
        eventBus.post(new RefundRequestEvent(order.getId(), order.getUserId(), amount));

        /* TODO remove onRefundComplete, it should be called on transaction finish! */
        onRefundComplete(order);
    }

    private void onRefundComplete(OrderTable order) {
        /* <ANY> -> REFUND */
        itemService.processRefund(order.getLastBookingId());
        OrderStatus statusRefund = statusRepository.findOrderStatusByName(IOrderStatusRepository.StatusNames.REFUND.name());
        order.setStatus(statusRefund);
        orderRepository.save(order);
    }

    private OrderTable deliveryEventCommonPreHandler(DeliveryStatusEvent event) {
        OrderTable order = getOrderOrThrow(event.getOrderId(), event.getUserId());
        switch (IOrderStatusRepository.StatusNames.valueOf(order.getStatus().getName())) {
            case PAID:
            case SHIPPING:
                break;
            default:
                throw new IllegalStateException();
        }
        return order;
    }

    private OrderTable paymentEventCommonPreHandler(PaymentStatusEvent event) {
        OrderTable order = getOrderOrThrow(event.getOrderId(), event.getUserId());
        if (IOrderStatusRepository.StatusNames.valueOf(order.getStatus().getName()) != IOrderStatusRepository.StatusNames.BOOKED) {
            throw new IllegalStateException();
        }
        return order;
    }

    private OrderTable getOrderOrThrow(UUID orderId, UUID userId) {
        Optional<OrderTable> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_ORDER, orderId);
            }
            throw new OrderNotFoundException("No order with id=" + orderId);
        }

        /* Basic user can access only theirs orders, admin can access any order */
        if (!orderOptional.get().getUserId().equals(userId) && !userService.getUserByID(userId).isAdmin()) {
            throw new OrderNotFoundException("No order with id=" + orderId);
        }
        return orderOptional.get();
    }
}
