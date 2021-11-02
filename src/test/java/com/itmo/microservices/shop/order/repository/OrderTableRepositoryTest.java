package com.itmo.microservices.shop.order.repository;

import com.itmo.microservices.shop.common.test.DataJpaTestCase;
import com.itmo.microservices.shop.order.HardcodedValues;
import com.itmo.microservices.shop.order.impl.entity.OrderItem;
import com.itmo.microservices.shop.order.impl.entity.OrderTable;
import com.itmo.microservices.shop.order.impl.repository.IOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("dev")
class OrderTableRepositoryTest extends DataJpaTestCase {
    @Autowired
    private IOrderTableRepository repository;

    private final HardcodedValues values = new HardcodedValues();
    private List<UUID> orderUUIDs;
    private List<UUID> userUUIDs;
    private final int n = 5;

    @BeforeEach
    void setUp() {
        orderUUIDs = new ArrayList<>();
        userUUIDs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            userUUIDs.add(UUID.randomUUID());
            OrderTable order = new OrderTable();
            order.setUserId(userUUIDs.get(i));
            order.setStatus(values.collectedStatus);
            order.setDeliveryDuration(values.slot);
            order.setOrderItems(new HashSet<>());
            repository.save(order);
            orderUUIDs.add(order.getId());
        }
    }

    @Test
    public void findById() {
        for (int i = 0; i < n; i++) {
            Optional<OrderTable> optionalOrder = repository.findById(orderUUIDs.get(i));
            assertFalse(optionalOrder.isEmpty());
            OrderTable order = optionalOrder.get();
            assertEquals(order.getId(), orderUUIDs.get(i));
            assertEquals(order.getUserId(), userUUIDs.get(i));
            assertEquals(order.getStatus(), values.collectedStatus);
            assertEquals(order.getDeliveryDuration(), values.slot);
            assertEquals(order.getOrderItems(), new HashSet<OrderItem>());
        }
    }
}
