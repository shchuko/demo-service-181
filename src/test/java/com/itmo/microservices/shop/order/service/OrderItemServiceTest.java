package com.itmo.microservices.shop.order.service;

import com.itmo.microservices.shop.common.test.DefaultSecurityTestCase;
import com.itmo.microservices.shop.order.HardcodedValues;
import com.itmo.microservices.shop.order.api.model.BookingDTO;
import com.itmo.microservices.shop.order.impl.entity.OrderItem;
import com.itmo.microservices.shop.order.impl.entity.OrderTable;
import com.itmo.microservices.shop.order.impl.mapper.OrderTableToOrderDTO;
import com.itmo.microservices.shop.order.impl.repository.IOrderItemRepository;
import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;
import com.itmo.microservices.shop.order.impl.repository.IOrderTableRepository;
import com.itmo.microservices.shop.order.impl.service.OrderItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
class OrderItemServiceTest extends DefaultSecurityTestCase {
    @MockBean
    private IOrderItemRepository itemRepository;
    @MockBean
    private IOrderStatusRepository statusRepository;
    @MockBean
    private IOrderTableRepository tableRepository;

    @Autowired
    private OrderItemService orderItemService;
    private final HardcodedValues values = new HardcodedValues();

    @BeforeEach
    void setUp() {
        Mockito.when(statusRepository.findOrderStatusByName("COLLECTING")).thenReturn(
                Optional.of(values.collectedStatus)
        );
        Mockito.when(statusRepository.findOrderStatusByName("BOOKED")).thenReturn(
                Optional.of(values.bookedStatus)
        );
    }
    @Test
    void createOrder() {
        Mockito.when(tableRepository.save(Mockito.any())).then(a -> {
            assertNotNull(a.getArgument(0));
            OrderTable order = new OrderTable();
            order.setUserId(values.userUUID);
            order.setId(values.orderUUID);
            order.setTimeCreated(Instant.now().getEpochSecond());
            order.setStatus(values.collectedStatus);
            assertEquals(values.userUUID, order.getUserId());
            assertEquals(values.orderUUID, order.getId());
            return order;
        });

        orderItemService.createOrder(values.userUUID);
    }

    @Test
    void getOrder() {
        OrderTable order = createOrderObject();
        Mockito.when(tableRepository.findById(values.orderUUID)).thenReturn(
                Optional.of(order)
        );

        assertEquals(OrderTableToOrderDTO.toDTO(order), orderItemService.getOrder(values.orderUUID));
    }

    @Test
    void addItem() {
        OrderTable order = createOrderObject();
        Mockito.when(itemRepository.save(Mockito.any())).then(
                a -> {
                    assertNotNull(a.getArgument(0));
                    OrderItem item = new OrderItem();
                    item.setItemId(values.itemUUID);
                    item.setOrderId(values.orderUUID);
                    item.setPrice(values.price);
                    item.setAmount(values.amount);
                    item.setOrder(order);

                    assertEquals(item.getItemId(), values.itemUUID);
                    assertEquals(item.getOrderId(), values.orderUUID);
                    assertEquals(item.getAmount(), values.amount);
                    assertEquals(item.getPrice(), values.price);
                    assertEquals(item.getOrder(), order);
                    return item;
                }
        );
        Mockito.when(tableRepository.findById(values.orderUUID)).thenReturn(
                Optional.of(order)
        );
        orderItemService.addItem(values.orderUUID, values.itemUUID, values.amount);
    }

    @Test
    void setTime() {
        OrderTable order = createOrderObject();
        Mockito.when(tableRepository.findById(values.orderUUID)).thenReturn(
                Optional.of(order)
        );

        Mockito.when(tableRepository.save(Mockito.any())).then(
                a -> {
                    assertNotNull(a.getArgument(0));
                    OrderTable savedOrder = new OrderTable();
                    savedOrder.setUserId(values.userUUID);
                    savedOrder.setId(values.orderUUID);
                    savedOrder.setTimeCreated(Instant.now().getEpochSecond());
                    savedOrder.setStatus(values.collectedStatus);
                    savedOrder.setDeliveryDuration(values.slot);
                    assertEquals(values.userUUID, savedOrder.getUserId());
                    assertEquals(values.orderUUID, savedOrder.getId());
                    assertEquals(values.slot, savedOrder.getDeliveryDuration());
                    return savedOrder;
                }
        );
        BookingDTO bookingDTO = orderItemService.setTime(values.orderUUID, values.slot);
    }

    @Test
    void finalizeOrder() {
        OrderTable order = createOrderObject();
        Mockito.when(tableRepository.findById(values.orderUUID)).thenReturn(
                Optional.of(order)
        );

        Mockito.when(tableRepository.save(Mockito.any())).then(
                a -> {
                    assertNotNull(a.getArgument(0));
                    OrderTable savedOrder = new OrderTable();
                    savedOrder.setUserId(values.userUUID);
                    savedOrder.setId(values.orderUUID);
                    savedOrder.setTimeCreated(Instant.now().getEpochSecond());
                    savedOrder.setStatus(values.bookedStatus);
                    savedOrder.setDeliveryDuration(values.slot);
                    assertEquals(values.userUUID, savedOrder.getUserId());
                    assertEquals(values.orderUUID, savedOrder.getId());
                    assertEquals(values.slot, savedOrder.getDeliveryDuration());
                    return savedOrder;
                }
        );
        BookingDTO bookingDTO = orderItemService.finalizeOrder(values.orderUUID);
    }

    private OrderTable createOrderObject(){
        OrderTable order = new OrderTable();
        order.setId(values.orderUUID);
        order.setTimeCreated(Instant.now().getEpochSecond());
        order.setStatus(values.bookedStatus);
        order.setUserId(values.userUUID);
        return order;
    }
}