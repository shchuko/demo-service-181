package com.itmo.microservices.shop.order.messaging;

import com.itmo.microservices.shop.order.api.model.OrderDTO;

public class OrderCreatedEvent {
    private final OrderDTO orderDTO;

    public OrderCreatedEvent(OrderDTO orderDTO) {
        this.orderDTO = orderDTO;
    }

    public OrderDTO getOrderDTO() {
        return orderDTO;
    }
}
