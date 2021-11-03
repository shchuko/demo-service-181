package com.itmo.microservices.shop.delivery.impl.util;

import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoModel;
import com.itmo.microservices.shop.delivery.impl.entity.DeliveryInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeliveryInfoConverterTest extends DefaultDeliveryInfo {

    @Test
    void canConvertToEntityProperly() {
        DeliveryInfoModel defaultInfoModel = createDefaultInfoModel();

        DeliveryInfo infoEntity = DeliveryInfoConverter.convertToEntity(defaultInfoModel);

        assertEquals(DEFAULT_ID, infoEntity.getId());
        assertEquals(DEFAULT_ORDER_ID, infoEntity.getOrderId());
        assertEquals(DEFAULT_SLOT, infoEntity.getSlot());
        assertEquals(DEFAULT_IS_DELIVERED_STATUS, infoEntity.getIsDelivered());
        assertEquals(DEFAULT_START_DELIVERY_AT, infoEntity.getStartDeliveryAt());
        assertEquals(DEFAULT_USER_ADDRESS, infoEntity.getAddress());
    }

    @Test
    void canConvertToModelProperly() {
        DeliveryInfo defaultInfoEntity = createDefaultInfoEntity();

        DeliveryInfoModel infoModel = DeliveryInfoConverter.convertToModel(defaultInfoEntity);


        assertEquals(DEFAULT_ID, infoModel.getId());
        assertEquals(DEFAULT_ORDER_ID, infoModel.getOrderId());
        assertEquals(DEFAULT_SLOT, infoModel.getSlot());
        assertEquals(DEFAULT_IS_DELIVERED_STATUS, infoModel.getIsDelivered());
        assertEquals(DEFAULT_START_DELIVERY_AT, infoModel.getStartDeliveryAt());
        assertEquals(DEFAULT_USER_ADDRESS, infoModel.getAddress());
    }


}