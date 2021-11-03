package com.itmo.microservices.shop.delivery.api.service;

import java.util.UUID;

public interface OrderServiceChecker {

    boolean isOrderExists(UUID orderId);
}
