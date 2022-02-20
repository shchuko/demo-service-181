package com.itmo.microservices.shop.order.impl.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException;
import com.itmo.microservices.shop.catalog.api.model.BookingLogRecordDTO;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.api.service.ItemService;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryTransactionFailedEvent;
import com.itmo.microservices.shop.delivery.api.messaging.DeliveryTransactionSuccessEvent;
import com.itmo.microservices.shop.order.api.exeptions.InvalidItemException;
import com.itmo.microservices.shop.order.api.messaging.OrderFailedPaidEvent;
import com.itmo.microservices.shop.order.api.messaging.OrderStartDeliveryTransactionEvent;
import com.itmo.microservices.shop.order.api.messaging.OrderSuccessPaidEvent;
import com.itmo.microservices.shop.order.api.model.BookingDTO;
import com.itmo.microservices.shop.order.api.model.OrderDTO;
import com.itmo.microservices.shop.order.api.service.IOrderService;
import com.itmo.microservices.shop.order.impl.entity.OrderItem;
import com.itmo.microservices.shop.order.impl.entity.OrderStatus;
import com.itmo.microservices.shop.order.impl.entity.OrderTable;
import com.itmo.microservices.shop.order.impl.logging.OrderServiceNotableEvent;
import com.itmo.microservices.shop.order.impl.mapper.OrderTableToOrderDTO;
import com.itmo.microservices.shop.order.impl.repository.IOrderItemRepository;
import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;
import com.itmo.microservices.shop.order.impl.repository.IOrderTableRepository;
import com.itmo.microservices.shop.order.messaging.OrderCreatedEvent;
import com.itmo.microservices.shop.payment.api.messaging.RefundOrderAnswerEvent;
import kotlin.Suppress;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Suppress(names = "UnstableApiUsage")
public class OrderItemService implements IOrderService {
    private final IOrderItemRepository itemRepository;
    private final IOrderStatusRepository statusRepository;
    private final IOrderTableRepository tableRepository;
    @InjectEventLogger
    private EventLogger eventLogger;
    private final EventBus eventBus;
    private final ItemService itemService;

    public OrderItemService(IOrderItemRepository itemRepository,
                            IOrderStatusRepository statusRepository,
                            IOrderTableRepository tableRepository,
                            EventBus eventBus, ItemService itemService) {
        this.itemRepository = itemRepository;
        this.statusRepository = statusRepository;
        this.tableRepository = tableRepository;
        this.eventBus = eventBus;
        this.itemService = itemService;
    }

    @Override
    public OrderDTO createOrder(UUID userUUID) throws NoSuchElementException {
        Optional<OrderStatus> collectingStatusOptional = statusRepository.findOrderStatusByName("COLLECTING");
        if (collectingStatusOptional.isEmpty()) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_STATUS, "COLLECTING");
            }
            throw new NoSuchElementException(String.format("No status with name %s", "COLLECTING"));
        }

        OrderTable order = new OrderTable();
        order.setTimeCreated(Instant.now().getEpochSecond());
        order.setStatus(collectingStatusOptional.get());
        order.setUserId(userUUID);
        tableRepository.save(order);

        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ORDER_CREATED, order.getId());
        }

        OrderDTO orderDTO = OrderTableToOrderDTO.toDTO(order);
        eventBus.post(new OrderCreatedEvent(orderDTO));
        return orderDTO;
    }

    @Override
    public OrderDTO getOrder(UUID orderUUID) throws NoSuchElementException {
        return OrderTableToOrderDTO.toDTO(getOrderByUUID(orderUUID));
    }

    @Override
    public void addItem(UUID orderUUID, UUID itemUUID, Integer amount) throws NoSuchElementException {
        OrderTable order = getOrderByUUID(orderUUID);

        if (!order.getStatus().getName().equals("BOOKED")){
            Optional<OrderStatus> collectingStatusOptional = statusRepository.findOrderStatusByName("COLLECTING");
            if (collectingStatusOptional.isEmpty()) {
                if (eventLogger != null) {
                    eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_STATUS, "COLLECTING");
                }
                throw new NoSuchElementException(String.format("No status with name %s", "COLLECTING"));
            }
            order.setStatus(collectingStatusOptional.get());
        }
        try {
            ItemDTO itemDTO = itemService.describeItem(itemUUID);
            OrderItem item = new OrderItem();
            item.setOrderId(orderUUID);
            item.setPrice(itemDTO.getPrice());
            item.setOrder(order);
            item.setAmount(amount);
            item.setItemId(itemUUID);
            itemRepository.save(item);
            if (eventLogger != null) {
                eventLogger.info(OrderServiceNotableEvent.I_ITEM_ADDED, itemUUID);
            }
        } catch (ItemNotFoundException exception) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_CAN_NOT_CONNECT_TO_ITEM_SERVICE, exception.getMessage());
            }
            throw new InvalidItemException(exception.getMessage());
        }
    }

    @Override
    public BookingDTO setTime(UUID orderUUID, Integer slot) throws NoSuchElementException {
        OrderTable order = getOrderByUUID(orderUUID);
        order.setDeliveryDuration(slot);
        tableRepository.save(order);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUuid(orderUUID);
        bookingDTO.setFailedItems(
                itemService.listBookingLogRecords(order.getLastBookingId()).stream()
                        .filter(it -> it.getStatus().equals("FAILED"))
                        .map(BookingLogRecordDTO::getItemId).collect(Collectors.toSet())
        );

        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ORDER_SET_TIME, orderUUID);
        }
        return bookingDTO;
    }

    @Override
    public BookingDTO finalizeOrder(UUID orderUUID) throws NoSuchElementException {
//        TODO will be updated to be in sync with catalog service updates
        throw new RuntimeException("Not implemented yet");
//        Optional<OrderStatus> finalizeStatusOptional = statusRepository.findOrderStatusByName("BOOKED");
//        if (finalizeStatusOptional.isEmpty()) {
//            if (eventLogger != null) {
//                eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_STATUS, "BOOKED");
//            }
//            throw new NoSuchElementException(String.format("No status with name %s", "BOOKED"));
//        }
//        try {
//            OrderTable order = getOrderByUUID(orderUUID);
//            if (Objects.equals(order.getStatus().getName(), "BOOKED")) {
//                if (eventLogger != null) {
//                    eventLogger.error(OrderServiceNotableEvent.E_ORDER_ALREADY_BOOKED, orderUUID);
//                }
//                throw new OrderAlreadyBookedException(String.format("Order with uuid %s already booked", orderUUID));
//            }
//            order.setStatus(finalizeStatusOptional.get());
//
//            HashMap<UUID, Integer> items = new HashMap<>();
//            Set<OrderItem> addedItems = order.getOrderItems();
//            for (OrderItem orderItem : addedItems) {
//                items.put(orderItem.getItemId(), orderItem.getAmount());
//            }
//            BookingDTO bookingDTO = itemService.book(items);
//            order.setLastBookingId(bookingDTO.getUuid());
//            tableRepository.save(order);
//            if (eventLogger != null) {
//                eventLogger.info(OrderServiceNotableEvent.I_ORDER_BOOKED, orderUUID);
//            }
//            eventBus.post(new OrderFinalizedEvent(OrderTableToOrderDTO.toDTO(order)));
//            return bookingDTO;
//        } catch (ItemNotFoundException exception) {
//            if (eventLogger != null) {
//                eventLogger.error(OrderServiceNotableEvent.E_CAN_NOT_CONNECT_TO_ITEM_SERVICE, exception.getMessage());
//            }
//            throw new NoSuchElementException(exception.getMessage());
//        }
    }

    @Subscribe
    public void handleSuccessDelivery(DeliveryTransactionSuccessEvent event) {
        OrderTable order = getOrderByUUID(event.getOrderId());
        Optional<OrderStatus> completedStatusOptional = statusRepository.findOrderStatusByName("COMPLETED ");
        if (completedStatusOptional.isEmpty()) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_STATUS, "COMPLETED ");
            }
            throw new NoSuchElementException(String.format("No status with name %s", "COMPLETED "));
        }
        order.setStatus(completedStatusOptional.get());
        tableRepository.save(order);
        if (eventLogger != null) {
            eventLogger.error(OrderServiceNotableEvent.I_ORDER_END_DELIVERY, event.getOrderId());
        }
    }

    @Subscribe
    public void handleStartDelivery(OrderStartDeliveryTransactionEvent event) {
        OrderTable order = getOrderByUUID(event.getOrderID());
        Optional<OrderStatus> shippingStatusOptional = statusRepository.findOrderStatusByName("SHIPPING ");
        if (shippingStatusOptional.isEmpty()) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_STATUS, "SHIPPING ");
            }
            throw new NoSuchElementException(String.format("No status with name %s", "SHIPPING "));
        }
        order.setStatus(shippingStatusOptional.get());
        tableRepository.save(order);
        if (eventLogger != null) {
            eventLogger.error(OrderServiceNotableEvent.I_ORDER_START_DELIVERY, event.getOrderID());
        }
    }

    @Subscribe
    public void handleFailedDelivery(DeliveryTransactionFailedEvent event) {
//        OrderTable order = getOrderByUUID(event.getOrderId());
//        Optional<OrderStatus> refundStatusOptional = statusRepository.findOrderStatusByName("REFUND ");
//        if (refundStatusOptional.isEmpty()) {
//            if (eventLogger != null) {
//                eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_STATUS, "REFUND ");
//            }
//            throw new NoSuchElementException(String.format("No status with name %s", "REFUND "));
//        }
//        order.setStatus(refundStatusOptional.get());
//        HashMap<UUID, Integer> items = new HashMap<>();
//        Set<OrderItem> bookedItems =  order.getOrderItems();
//        for (OrderItem orderItem : bookedItems) {
//            items.put(orderItem.getItemId(), orderItem.getAmount());
//        }
//        itemService.cancelBooking(order.getLastBookingId());
//        eventBus.post(new RefundOrderRequestEvent(event.getOrderId(), new Double(getAmount(event.getOrderId()))));
//        if (eventLogger != null) {
//            eventLogger.error(OrderServiceNotableEvent.I_ORDER_FAILED_DELIVERY, event.getOrderId());
//        }
    }

    @Subscribe
    public void handleSuccessPaid(OrderSuccessPaidEvent event) {
        Optional<OrderStatus> paidStatusOptional = statusRepository.findOrderStatusByName("PAID");
        if (paidStatusOptional.isEmpty()) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_STATUS, "PAID");
            }
            throw new NoSuchElementException(String.format("No status with name %s", "PAID"));
        }
        OrderTable order = getOrderByUUID(event.getOrderID());
        order.setStatus(paidStatusOptional.get());
        tableRepository.save(order);
    }

    @Subscribe
    public void handleFailedPaid(OrderFailedPaidEvent event) {
        Optional<OrderStatus> discardStatusOptional = statusRepository.findOrderStatusByName("DISCARD");
        if (discardStatusOptional.isEmpty()) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_STATUS, "DISCARD");
            }
            throw new NoSuchElementException(String.format("No status with name %s", "DISCARD"));
        }
        OrderTable order = getOrderByUUID(event.getOrderID());
        order.setStatus(discardStatusOptional.get());
        tableRepository.save(order);
        itemService.cancelBooking(order.getLastBookingId());
    }

    @Subscribe
    public void handleRefundAnswer(RefundOrderAnswerEvent event) {
//        if (PaymentStatusRepository.VALUES.FAILED.name().equals(event.getRefundStatus())) {
//        } else {
//            Optional<OrderStatus> refundStatusOptional = statusRepository.findOrderStatusByName("REFUND");
//            if (refundStatusOptional.isEmpty()) {
//                if (eventLogger != null) {
//                    eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_STATUS, "REFUND");
//                }
//                throw new NoSuchElementException(String.format("No status with name %s", "REFUND"));
//            }
//            OrderTable order = getOrderByUUID(event.getOrderUUID());
//            order.setStatus(refundStatusOptional.get());
//            tableRepository.save(order);
//            itemService.cancelBooking(order.getLastBookingId());
//        }
    }

    public Integer getAmount(UUID orderUUID) throws NoSuchElementException{
        OrderTable orderTable = this.getOrderByUUID(orderUUID);
        int amount = 0;
        for (OrderItem item : orderTable.getOrderItems()) {
            try {
                ItemDTO itemDTO = itemService.describeItem(item.getItemId());
                amount += itemDTO.getAmount();
            } catch (ItemNotFoundException exception) {
                if (eventLogger != null) {
                    eventLogger.error(OrderServiceNotableEvent.E_CAN_NOT_CONNECT_TO_ITEM_SERVICE, exception.getMessage());
                }
                throw new NoSuchElementException(String.format(exception.getMessage()));
            }
        }
        return amount;
    }

    private OrderTable getOrderByUUID(UUID orderUUID) {
        Optional<OrderTable> orderTableOptional = tableRepository.findById(orderUUID);
        if (orderTableOptional.isEmpty()) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_ORDER, orderUUID);
            }
            throw new NoSuchElementException(String.format("No order with uuid %s", orderUUID));
        }
        return orderTableOptional.get();
    }
}
