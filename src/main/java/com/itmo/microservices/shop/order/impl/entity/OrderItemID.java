package com.itmo.microservices.shop.order.impl.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Embeddable;

@Embeddable
public class OrderItemID implements Serializable {

  public UUID orderId;
  public UUID itemId;

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
