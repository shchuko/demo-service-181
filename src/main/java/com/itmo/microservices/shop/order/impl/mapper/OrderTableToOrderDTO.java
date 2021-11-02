package com.itmo.microservices.shop.order.impl.mapper;

import com.itmo.microservices.shop.order.api.model.OrderDTO;
import com.itmo.microservices.shop.order.api.model.OrderItemDTO;
import com.itmo.microservices.shop.order.impl.entity.OrderItem;
import com.itmo.microservices.shop.order.impl.entity.OrderTable;

import java.util.HashMap;

public class OrderTableToOrderDTO {
    public static OrderDTO toDTO(OrderTable orderTable){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUuid(orderTable.getId());
        orderDTO.setStatus(orderTable.getStatus().getName());
        if (orderTable.getDeliveryDuration() != null) {
            orderDTO.setDeliveryDuration(orderTable.getDeliveryDuration());
        }
        orderDTO.setTimeCreated(orderTable.getTimeCreated());

        HashMap<OrderItemDTO, Integer> orderItems = new HashMap<>();
        if (orderTable.getOrderItems() != null) {
            for (OrderItem item : orderTable.getOrderItems()) {
                orderItems.put(OrderItemToOrderItemDTO.toDTO(item), item.getAmount());
            }
        }
        orderDTO.setItemsMap(orderItems);
        return orderDTO;
    }
}
