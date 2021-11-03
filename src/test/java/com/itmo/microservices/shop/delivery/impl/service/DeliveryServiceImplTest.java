package com.itmo.microservices.shop.delivery.impl.service;

import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoModel;
import com.itmo.microservices.shop.delivery.api.service.DeliveryService;
import com.itmo.microservices.shop.delivery.impl.entity.DeliveryInfo;
import com.itmo.microservices.shop.delivery.impl.repository.DeliveryInfoRepository;
import com.itmo.microservices.shop.delivery.impl.util.DefaultDeliveryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class DeliveryServiceImplTest {
    private DefaultDeliveryInfo DefaultDeliveryInfo = new DefaultDeliveryInfo();
    private DeliveryInfoRepository repository;
    private DeliveryService deliveryService;
    static final List<Integer> defaultTimeSlots = Arrays.asList(1, 25, 35, 16, 100, 4);


    @BeforeEach
    void init() {
        repository = Mockito.mock(DeliveryInfoRepository.class);
        Mockito.doReturn(defaultTimeSlots).when(repository).getDeliveryTimeSlots();

        deliveryService = new DeliveryServiceImpl(repository);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 4})
    void canGetFixedNumberOfTimeSlots(int timeSlotsNum) {
        List<Integer> timeSlots = deliveryService.getDeliverySlots(timeSlotsNum);
        assertNotNull(timeSlots);
        assertEquals(timeSlotsNum, timeSlots.size());
        for (int i = 0; i < timeSlotsNum; i++) {
            int expected = defaultTimeSlots.get(i);
            int actual = timeSlots.get(i);
            assertEquals(expected, actual);
        }
    }

    @Test
    void cantGetMoreThatExistedTimeSlots() {
        List<Integer> timeSlots = deliveryService.getDeliverySlots(100);
        assertNotNull(timeSlots);
        assertEquals(defaultTimeSlots.size(), timeSlots.size());
        assertEquals(defaultTimeSlots, timeSlots);
    }

    @Test
    void canSetTimeSlot() {
        DeliveryInfoModel defaultModel = DefaultDeliveryInfo.createDefaultInfoModel();

        Mockito.when(repository.save(Mockito.any())).then(a -> {
            assertNotNull(a.getArgument(0));
            DeliveryInfo entity = (DeliveryInfo) a.getArgument(0);
            assertEquals(DefaultDeliveryInfo.DEFAULT_ORDER_ID, entity.getOrderId());
            assertEquals(DefaultDeliveryInfo.DEFAULT_SLOT, entity.getSlot());
            assertEquals(DefaultDeliveryInfo.DEFAULT_IS_DELIVERED_STATUS, entity.getIsDelivered());
            assertEquals(DefaultDeliveryInfo.DEFAULT_USER_ADDRESS, entity.getAddress());
            return entity;
        });

        deliveryService.setTimeSlot(defaultModel);
    }

    @Test
    void canGetDeliveryInfo() {
        Map<UUID, DeliveryInfo> entityByOrderId = new HashMap<>();
        Mockito.when(repository.save(Mockito.any())).then(a -> {
            final DeliveryInfo entity = ((DeliveryInfo) a.getArgument(0));
            entityByOrderId.put(entity.getOrderId(), entity);
            return entity;
        });
        Mockito.when(repository.findDeliveryInfoByOrderId(DefaultDeliveryInfo.DEFAULT_ORDER_ID)).then(a -> {
            final UUID orderId = (UUID) a.getArgument(0);
            final DeliveryInfo entity = entityByOrderId.getOrDefault(orderId, null);
            return Optional.ofNullable(entity);
        });


        DeliveryInfoModel defaultModel = DefaultDeliveryInfo.createDefaultInfoModel();
        deliveryService.setTimeSlot(defaultModel);

        DeliveryInfoModel receivedModel = deliveryService.getDeliveryInfo(defaultModel.getOrderId());

        assertNotNull(receivedModel);
        assertEquals(DefaultDeliveryInfo.DEFAULT_ORDER_ID, receivedModel.getOrderId());
        assertEquals(DefaultDeliveryInfo.DEFAULT_SLOT, receivedModel.getSlot());
        assertEquals(DefaultDeliveryInfo.DEFAULT_IS_DELIVERED_STATUS, receivedModel.getIsDelivered());
        assertEquals(DefaultDeliveryInfo.DEFAULT_USER_ADDRESS, receivedModel.getAddress());
    }
}