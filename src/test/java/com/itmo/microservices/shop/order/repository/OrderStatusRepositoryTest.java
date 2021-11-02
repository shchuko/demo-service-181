package com.itmo.microservices.shop.order.repository;

import com.itmo.microservices.shop.common.test.DataJpaTestCase;
import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("dev")
class OrderStatusRepositoryTest extends DataJpaTestCase {
    @Autowired
    private IOrderStatusRepository repository;
    @Test
    void findOrderStatusByName() {
        assertFalse(repository.findOrderStatusByName("COLLECTING").isEmpty());
        assertFalse(repository.findOrderStatusByName("DISCARD").isEmpty());
        assertFalse(repository.findOrderStatusByName("BOOKED").isEmpty());
        assertFalse(repository.findOrderStatusByName("PAID").isEmpty());
        assertFalse(repository.findOrderStatusByName("SHIPPING").isEmpty());
        assertFalse(repository.findOrderStatusByName("REFUND").isEmpty());
    }
}