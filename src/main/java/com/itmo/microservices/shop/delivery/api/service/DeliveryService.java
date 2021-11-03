package com.itmo.microservices.shop.delivery.api.service;

import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoModel;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {
    /**
     * @param num - number of slots we santed to get
     */
    List<Integer> getDeliverySlots(int num);

    List<Integer> getDeliverySlots();


    DeliveryInfoModel setTimeSlot(DeliveryInfoModel deliveryInfoModel);

    DeliveryInfoModel getDeliveryInfo(UUID orderId);
}
