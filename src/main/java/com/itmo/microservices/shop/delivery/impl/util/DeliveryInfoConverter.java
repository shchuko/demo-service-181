package com.itmo.microservices.shop.delivery.impl.util;

import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoModel;
import com.itmo.microservices.shop.delivery.impl.entity.DeliveryInfo;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DeliveryInfoConverter {

    public static DeliveryInfo convertToEntity(@NotNull DeliveryInfoModel from) {
        DeliveryInfo to = new DeliveryInfo();
        to.setId(from.getId());
        to.setOrderId(from.getOrderId());
        to.setIsDelivered(from.getIsDelivered());
        to.setStartDeliveryAt(from.getStartDeliveryAt());
        to.setAddress(from.getAddress());
        to.setSlot(from.getSlot());

        return to;
    }

    public static DeliveryInfoModel convertToModel(@NotNull DeliveryInfo from) {
        DeliveryInfoModel to = new DeliveryInfoModel();

        to.setId(from.getId());
        to.setOrderId(from.getOrderId());
        to.setIsDelivered(from.getIsDelivered());
        to.setStartDeliveryAt(from.getStartDeliveryAt());
        to.setAddress(from.getAddress());
        to.setSlot(from.getSlot());

        return to;
    }
}
