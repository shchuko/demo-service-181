package com.itmo.microservices.shop.order;

import com.itmo.microservices.shop.order.impl.entity.OrderStatus;
import com.itmo.microservices.shop.order.impl.repository.IOrderStatusRepository;

import java.util.UUID;

public class HardcodedValues {
    public final UUID orderUUID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
    public final UUID userUUID = UUID.fromString("3fa85f64-5720-4562-b3fc-2c963f66afa6");
    public final UUID itemUUID = UUID.fromString("3fa75f64-5720-4562-b3fc-2c963f66afa6");

    public final Integer price = 1000;
    public final Integer amount = 2;
    public final Integer slot = 160256;
    public final Long time = 456L;

    public final OrderStatus collectedStatus;
    public final OrderStatus bookedStatus;

    {
        collectedStatus = new OrderStatus();
        collectedStatus.setId(1);
        collectedStatus.setName(IOrderStatusRepository.StatusNames.COLLECTING.name());

        bookedStatus = new OrderStatus();
        bookedStatus.setId(2);
        bookedStatus.setName(IOrderStatusRepository.StatusNames.BOOKED.name());
    }
}