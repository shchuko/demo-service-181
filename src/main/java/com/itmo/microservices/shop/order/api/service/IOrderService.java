package com.itmo.microservices.shop.order.api.service;

import com.itmo.microservices.shop.order.api.model.BookingDTO;
import com.itmo.microservices.shop.order.api.model.OrderDTO;

import java.util.UUID;


public interface IOrderService {
    OrderDTO createOrder();
    OrderDTO getOrder(UUID orderUUID);
    void addItem(UUID orderUUID, UUID itemUUID, Integer amount);
    BookingDTO setTime(UUID orderUUID, Integer slot);
    BookingDTO finalizeOrder(UUID orderUUID);
}
