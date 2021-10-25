package com.itmo.microservices.shop.payment.impl.entity;

import java.util.Objects;
import java.util.UUID;
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
public class FinancialOperationType {

  @Id
  private UUID uuid;
  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FinancialOperationType financialOperationType = (FinancialOperationType) o;
    return Objects.equals(uuid, financialOperationType.getUuid());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(uuid);
  }
}
