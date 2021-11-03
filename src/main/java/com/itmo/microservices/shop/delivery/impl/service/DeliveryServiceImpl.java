package com.itmo.microservices.shop.delivery.impl.service;

import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.shop.common.exception.NotFoundException;
import com.itmo.microservices.shop.delivery.api.model.DeliveryInfoModel;
import com.itmo.microservices.shop.delivery.api.service.DeliveryService;
import com.itmo.microservices.shop.delivery.api.service.OrderServiceChecker;
import com.itmo.microservices.shop.delivery.impl.entity.DeliveryInfo;
import com.itmo.microservices.shop.delivery.impl.repository.DeliveryInfoRepository;
import com.itmo.microservices.shop.delivery.impl.util.DeliveryInfoConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryServiceImpl implements DeliveryService, OrderServiceChecker {
    private final DeliveryInfoRepository repository;
    @InjectEventLogger
    private EventLogger eventLogger;

    DeliveryServiceImpl(DeliveryInfoRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<Integer> getDeliverySlots(int num) {
        return repository.getDeliveryTimeSlots()
                .stream()
                .limit(num) // TODO :
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getDeliverySlots() {
        return repository.getDeliveryTimeSlots();
    }

    @Override
    public DeliveryInfoModel setTimeSlot(DeliveryInfoModel deliveryInfoModel) {
        if (!isOrderExists(deliveryInfoModel.getOrderId())) {
            throw new NotFoundException("Order id = " + deliveryInfoModel.getOrderId() + " is doesn't exist!");
        }

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setOrderId(deliveryInfoModel.getOrderId());
        deliveryInfo.setSlot(deliveryInfoModel.getSlot());
        deliveryInfo.setIsDelivered(false);
        deliveryInfo.setAddress(deliveryInfoModel.getAddress());
        deliveryInfo.setStartDeliveryAt(0);
        deliveryInfo = repository.save(deliveryInfo);
        return DeliveryInfoConverter.convertToModel(deliveryInfo);
    }

    @Override
    public DeliveryInfoModel getDeliveryInfo(UUID orderId) {
        return DeliveryInfoConverter.convertToModel(getAndCheckDeliveryInfo(orderId));
    }

    private DeliveryInfo getAndCheckDeliveryInfo(UUID orderId) {
        DeliveryInfo deliveryInfo = repository.findDeliveryInfoByOrderId(orderId).orElse(null);
        if (deliveryInfo == null) {

            throw new NotFoundException("Can't find deliveryEntity by orderId");
        }
        return deliveryInfo;
    }

    @Override
    public boolean isOrderExists(UUID orderId) {
        //TODO implement checking logic
        return true;
    }
}
