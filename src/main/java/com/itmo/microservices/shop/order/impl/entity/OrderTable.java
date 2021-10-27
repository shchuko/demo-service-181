package com.itmo.microservices.shop.order.impl.entity;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class OrderTable {

  @Id
  private UUID uuid;

  private Long timeCreated;
  private Integer deliveryDuration;
  private String username;

  @ManyToOne
  private OrderStatus orderStatus;

  @OneToMany
  @Exclude
  private Set<OrderItem> items;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    OrderTable orderTable = (OrderTable) o;
    return Objects.equals(uuid, orderTable.getUuid());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(uuid);
  }
}