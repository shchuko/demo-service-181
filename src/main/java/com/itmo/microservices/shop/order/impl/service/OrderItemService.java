package com.itmo.microservices.shop.order.impl.service;

import com.google.common.eventbus.EventBus;
import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.catalog.api.service.ItemService;
import com.itmo.microservices.shop.order.api.exeptions.OrderAlreadyBookedException;
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
import com.itmo.microservices.shop.order.messaging.OrderFinalizedEvent;
import kotlin.Suppress;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

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
    public OrderDTO createOrder() throws NoSuchElementException {
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
        order.setUserId(UUID.randomUUID()); // mock user UUID
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
        try {
           ItemDTO itemDTO = itemService.getByUuid(itemUUID);
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
        }
        catch (ItemNotFoundException exception) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_CAN_NOT_CONNECT_TO_ITEM_SERVICE, exception.getMessage());
            }
            throw new NoSuchElementException(exception.getMessage());
        }
    }

    @Override
    public BookingDTO setTime(UUID orderUUID, Integer slot) throws NoSuchElementException {
        OrderTable order = getOrderByUUID(orderUUID);
        order.setDeliveryDuration(slot);
        tableRepository.save(order);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUuid(orderUUID);
        bookingDTO.setFailedItems(new HashSet<>());

        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ORDER_SET_TIME, orderUUID);
        }
        return bookingDTO;
    }

    @Override
    public BookingDTO finalizeOrder(UUID orderUUID) throws NoSuchElementException {
        Optional<OrderStatus> finalizeStatusOptional = statusRepository.findOrderStatusByName("BOOKED");
        if (finalizeStatusOptional.isEmpty()) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_NO_SUCH_STATUS, "BOOKED");
            }
            throw new NoSuchElementException(String.format("No status with name %s", "BOOKED"));
        }
        try {
            OrderTable order = getOrderByUUID(orderUUID);
            if (Objects.equals(order.getStatus().getName(), "BOOKED")) {
                if (eventLogger != null) {
                    eventLogger.error(OrderServiceNotableEvent.E_ORDER_ALREADY_BOOKED, orderUUID);
                }
                throw new OrderAlreadyBookedException(String.format("Order with uuid %s already booked", orderUUID));
            }
            order.setStatus(finalizeStatusOptional.get());

            HashMap<UUID, Integer> items = new HashMap<>();
            Set<OrderItem> addedItems =  order.getOrderItems();
            for (OrderItem orderItem : addedItems) {
                items.put(orderItem.getItemId(), orderItem.getAmount());
            }
            List<UUID> failedItems = itemService.bookItems(items);

            BookingDTO bookingDTO = new BookingDTO();
            bookingDTO.setUuid(orderUUID);
            bookingDTO.setFailedItems(new HashSet<>(failedItems));

            tableRepository.save(order);
            if (eventLogger != null) {
                eventLogger.info(OrderServiceNotableEvent.I_ORDER_BOOKED, orderUUID);
            }
            eventBus.post(new OrderFinalizedEvent(OrderTableToOrderDTO.toDTO(order)));
            return bookingDTO;
        }
        catch (ItemNotFoundException exception) {
            if (eventLogger != null) {
                eventLogger.error(OrderServiceNotableEvent.E_CAN_NOT_CONNECT_TO_ITEM_SERVICE, exception.getMessage());
            }
            throw new NoSuchElementException(exception.getMessage());
        }
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
