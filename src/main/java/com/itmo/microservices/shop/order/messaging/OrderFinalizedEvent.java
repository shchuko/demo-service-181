package com.itmo.microservices.shop.order.messaging;

import com.itmo.microservices.shop.order.api.model.OrderDTO;

public class OrderFinalizedEvent {
    private final OrderDTO orderDTO;

    public OrderFinalizedEvent(OrderDTO orderDTO) {
        this.orderDTO = orderDTO;
    }

    public OrderDTO getOrderDTO() {
        return orderDTO;
    }
}
