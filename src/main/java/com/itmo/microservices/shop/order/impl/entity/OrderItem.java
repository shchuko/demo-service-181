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
  private UUID uuid;

  @ManyToOne
  private OrderTable order;
  private Integer amount;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderItem orderItem = (OrderItem) o;
    return Objects.equals(uuid, orderItem.getUuid());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(uuid);
  }
}
