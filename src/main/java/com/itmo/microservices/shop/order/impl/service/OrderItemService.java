package com.itmo.microservices.shop.order.impl.service;

import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderItemService implements IOrderService {
    private final IOrderItemRepository itemRepository;
    private final IOrderStatusRepository statusRepository;
    private final IOrderTableRepository tableRepository;
    @InjectEventLogger
    private EventLogger eventLogger;

    public OrderItemService(IOrderItemRepository itemRepository,
                            IOrderStatusRepository statusRepository,
                            IOrderTableRepository tableRepository) {
        this.itemRepository = itemRepository;
        this.statusRepository = statusRepository;
        this.tableRepository = tableRepository;
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
        return OrderTableToOrderDTO.toDTO(order);
    }

    @Override
    public OrderDTO getOrder(UUID orderUUID) throws NoSuchElementException {
        return OrderTableToOrderDTO.toDTO(getOrderByUUID(orderUUID));
    }

    @Override
    public void addItem(UUID orderUUID, UUID itemUUID, Integer amount) throws NoSuchElementException {
        OrderTable order = getOrderByUUID(orderUUID);
        OrderItem item = new OrderItem();
        item.setOrderId(orderUUID);
        item.setPrice(0); // mock price
        item.setOrder(order);
        item.setAmount(amount);
        item.setItemId(itemUUID);
        itemRepository.save(item);

        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ITEM_ADDED, itemUUID);
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
        return bookingDTO; // mock BookingDTO
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

        OrderTable order = getOrderByUUID(orderUUID);
        order.setStatus(finalizeStatusOptional.get());
        tableRepository.save(order);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUuid(orderUUID);
        bookingDTO.setFailedItems(new HashSet<>());

        if (eventLogger != null) {
            eventLogger.info(OrderServiceNotableEvent.I_ORDER_BOOKED, orderUUID);
        }
        return bookingDTO; // mock BookingDTO
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
