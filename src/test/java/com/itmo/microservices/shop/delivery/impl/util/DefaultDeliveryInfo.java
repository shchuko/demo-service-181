package com.itmo.microservices.shop.delivery.impl.util;

import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoModel;
import com.itmo.microservices.shop.delivery.impl.entity.DeliveryInfo;

import java.util.UUID;

public class DefaultDeliveryInfo {
    static public final UUID DEFAULT_ID = UUID.randomUUID();
    static public final UUID DEFAULT_ORDER_ID = UUID.randomUUID();
    static public final int DEFAULT_SLOT = 165;
    static public final boolean DEFAULT_IS_DELIVERED_STATUS = false;
    static public final long DEFAULT_START_DELIVERY_AT = 150L;
    static public final String DEFAULT_USER_ADDRESS = "Moscow";

    static public DeliveryInfoModel createDefaultInfoModel() {
        DeliveryInfoModel infoModel = new DeliveryInfoModel();
        infoModel.setId(DEFAULT_ID);
        infoModel.setOrderId(DEFAULT_ORDER_ID);
        infoModel.setSlot(DEFAULT_SLOT);
        infoModel.setIsDelivered(DEFAULT_IS_DELIVERED_STATUS);
        infoModel.setStartDeliveryAt(DEFAULT_START_DELIVERY_AT);
        infoModel.setAddress(DEFAULT_USER_ADDRESS);
        return infoModel;
    }

    static public DeliveryInfo createDefaultInfoEntity() {
        DeliveryInfo infoEntity = new DeliveryInfo();
        infoEntity.setId(DEFAULT_ID);
        infoEntity.setOrderId(DEFAULT_ORDER_ID);
        infoEntity.setSlot(DEFAULT_SLOT);
        infoEntity.setIsDelivered(DEFAULT_IS_DELIVERED_STATUS);
        infoEntity.setStartDeliveryAt(DEFAULT_START_DELIVERY_AT);
        infoEntity.setAddress(DEFAULT_USER_ADDRESS);
        return infoEntity;
    }

}
