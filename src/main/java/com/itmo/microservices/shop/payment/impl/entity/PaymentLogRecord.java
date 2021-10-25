package com.itmo.microservices.shop.payment.impl.entity;

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
public class PaymentLogRecord {

  @Id
  private UUID uuid;
  private Integer amount;
  private Long timestamp;
  private UUID orderId;
  private UUID transactionId;
  private String username;

  @ManyToOne
  private PaymentStatus paymentStatus;

  @ManyToOne
  private FinancialOperationType financialOperationType;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentLogRecord paymentLogRecord = (PaymentLogRecord) o;
    return Objects.equals(uuid, paymentLogRecord.getUuid());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(uuid);
  }
}
