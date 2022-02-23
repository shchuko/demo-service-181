package com.itmo.microservices.shop.order.impl.mapper;

import com.itmo.microservices.shop.order.api.model.OrderDTO;
import com.itmo.microservices.shop.order.api.model.PaymentLogRecord;
import com.itmo.microservices.shop.order.impl.entity.OrderItem;
import com.itmo.microservices.shop.order.impl.entity.OrderTable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class OrderTableToOrderDTO {
    public static OrderDTO toDTO(OrderTable orderTable, List<PaymentLogRecord> paymentHistory) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUuid(orderTable.getId());
        orderDTO.setStatus(orderTable.getStatus().getName());
        if (orderTable.getDeliveryDuration() != null) {
            orderDTO.setDeliveryDuration(orderTable.getDeliveryDuration());
        }
        orderDTO.setTimeCreated(orderTable.getTimeCreated());

        HashMap<UUID, Integer> orderItems = new HashMap<>();
        if (orderTable.getOrderItems() != null) {
            for (OrderItem item : orderTable.getOrderItems()) {
                orderItems.put(item.getItemId(), item.getAmount());
            }
        }
        orderDTO.setItemsMap(orderItems);
        orderDTO.setPaymentHistory(paymentHistory);
        return orderDTO;
    }
}
