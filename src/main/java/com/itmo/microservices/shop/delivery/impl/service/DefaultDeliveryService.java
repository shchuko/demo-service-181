package com.itmo.microservices.shop.delivery.impl.service;

import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.commonlib.logging.NotableEvent;
import com.itmo.microservices.shop.delivery.api.service.DeliveryService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itmo.microservices.shop.delivery.impl.logging.DeliveryServiceNotableEvents.DELIVERY_SLOTS_REQUESTED;
import static com.itmo.microservices.shop.delivery.impl.logging.DeliveryServiceNotableEvents.DELIVERY_SLOTS_REQUESTED_LIMITED;

@Service
public class DefaultDeliveryService implements DeliveryService {
    @InjectEventLogger
    private EventLogger eventLogger;

    @NotNull
    @Override
    public List<Integer> getDeliverySlots(int number) {
        logInfoEvent(DELIVERY_SLOTS_REQUESTED_LIMITED, "" + number);
        return getDeliverySlots().stream().limit(number).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<Integer> getDeliverySlots() {
        logInfoEvent(DELIVERY_SLOTS_REQUESTED);
        /* Stub: Generate numbers 1..30 */
        return Stream.iterate(1, n -> n + 1)
                .limit(30)
                .collect(Collectors.toList());
    }

    private void logInfoEvent(@NotNull NotableEvent event, @NotNull Object... payload) {
        if (eventLogger != null) {
            eventLogger.info(event, payload);
        }
    }
}
