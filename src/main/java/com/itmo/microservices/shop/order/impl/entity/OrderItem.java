package com.itmo.microservices.shop.order.impl.entity;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class OrderItem {

  @Id
  private Long id;
  private UUID itemId;

  @ManyToOne
  private OrderTable order;
  private Integer amount;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    OrderItem orderItem = (OrderItem) o;
    return id != null && Objects.equals(id, orderItem.id) && Objects.equals(itemId,
        orderItem.itemId) && Objects.equals(order, orderItem.order) && Objects.equals(amount,
        orderItem.amount);
  }

  @Override
  public int hashCode() {
    return id.hashCode() * itemId.hashCode() * order.hashCode() * amount.hashCode();
  }
}
