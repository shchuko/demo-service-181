package com.itmo.microservices.shop.order.impl.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class OrderTable {

  @Id
  @GeneratedValue
  private UUID id;
  private Long timeCreated;
  private Integer deliveryDuration;
  private Integer deliverySlot;
  private UUID userId;
  private UUID lastBookingId;

  @ManyToOne(fetch = FetchType.EAGER)
  private OrderStatus status;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "orderId")
  private Set<OrderItem> orderItems;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    OrderTable orderTable = (OrderTable) o;
    return Objects.equals(id, orderTable.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    /* Using vanilla Java instead of lombok because of LAZY fetch type */
    return "OrderTable(" +
        "id=" + id +
        ", timeCreated=" + timeCreated +
        ", deliveryDuration=" + deliveryDuration +
        ", userId=" + userId +
        ", status=" + (Hibernate.isInitialized(status) ? status : "<NOT_FETCHED>") +
        ", orderItems=" + (Hibernate.isInitialized(orderItems) ? orderItems : "<NOT_FETCHED>") +
        ")";
  }
}
