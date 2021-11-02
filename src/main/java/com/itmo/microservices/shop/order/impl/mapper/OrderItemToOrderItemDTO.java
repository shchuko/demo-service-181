package com.itmo.microservices.shop.order.impl.mapper;

import com.itmo.microservices.shop.order.api.model.OrderItemDTO;
import com.itmo.microservices.shop.order.impl.entity.OrderItem;

public class OrderItemToOrderItemDTO {
    public static OrderItemDTO toDTO(OrderItem item) {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setId(item.getItemId());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setTitle("mock name"); // mock
        return itemDTO;
    }
}
