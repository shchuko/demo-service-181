package com.itmo.microservices.shop.delivery.impl.entity;

import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
public class DeliveryInfo {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID orderId;

    private Boolean isDelivered;

    private long startDeliveryAt;

    private String address;

    private int slot;

    public DeliveryInfo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        DeliveryInfo that = (DeliveryInfo) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Boolean getIsDelivered() {
        return isDelivered;
    }

    public long getStartDeliveryAt() {
        return startDeliveryAt;
    }

    public String getAddress() {
        return address;
    }

    public int getSlot() {
        return slot;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setIsDelivered(Boolean delivered) {
        isDelivered = delivered;
    }

    public void setStartDeliveryAt(long startDeliveryAt) {
        this.startDeliveryAt = startDeliveryAt;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}