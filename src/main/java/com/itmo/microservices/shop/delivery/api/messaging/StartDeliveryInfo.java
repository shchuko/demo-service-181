package com.itmo.microservices.shop.delivery.api.messaging;

public class StartDeliveryInfo {

    private final StartDeliveryEvent event;
    private final long startTime;

    public StartDeliveryEvent getEvent() {
        return event;
    }

    public long getStartTime() {
        return startTime;
    }

    public StartDeliveryInfo(StartDeliveryEvent event, long startTime) {
        this.event = event;
        this.startTime = startTime;
    }
}
