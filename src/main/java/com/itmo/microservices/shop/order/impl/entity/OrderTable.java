package com.itmo.microservices.shop.order.impl.entity;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class OrderTable {

  @Id
  private UUID id;
  private Long timeCreated;
  private Integer deliveryDuration;
  private UUID userId;

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
