package com.itmo.microservices.shop.order.repository;

import com.itmo.microservices.shop.common.test.DataJpaTestCase;
import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
class OrderStatusRepositoryTest extends DataJpaTestCase {
    @Autowired
    private IOrderStatusRepository repository;
    @Test
    void findOrderStatusByName() {
        repository.findOrderStatusByName(IOrderStatusRepository.StatusNames.COLLECTING.name());
        repository.findOrderStatusByName(IOrderStatusRepository.StatusNames.DISCARD.name());
        repository.findOrderStatusByName(IOrderStatusRepository.StatusNames.BOOKED.name());
        repository.findOrderStatusByName(IOrderStatusRepository.StatusNames.PAID.name());
        repository.findOrderStatusByName(IOrderStatusRepository.StatusNames.SHIPPING.name());
        repository.findOrderStatusByName(IOrderStatusRepository.StatusNames.REFUND.name());
    }
}