package com.itmo.microservices.shop.payment.impl.entity;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
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
public class PaymentStatus {

  @Id
  private Integer id;
  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    PaymentStatus paymentStatus = (PaymentStatus) o;
    return Objects.equals(id, paymentStatus.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
