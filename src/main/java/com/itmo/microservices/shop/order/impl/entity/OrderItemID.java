package com.itmo.microservices.shop.order.impl.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class OrderItemID implements Serializable {

    public UUID orderId;
    public UUID itemId;

    public OrderItemID() {
    }

    public OrderItemID(UUID orderId, UUID itemId) {
        this.orderId = orderId;
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderItemID that = (OrderItemID) o;
    return Objects.equals(orderId, that.orderId)
        && Objects.equals(itemId, that.itemId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderId, itemId);
  }
}
