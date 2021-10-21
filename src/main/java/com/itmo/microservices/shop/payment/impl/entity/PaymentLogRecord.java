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
  private Long id;
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
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    PaymentLogRecord that = (PaymentLogRecord) o;
    return id != null && Objects.equals(id, that.id) && Objects.equals(amount, that.amount)
        && Objects.equals(timestamp, that.timestamp) && Objects.equals(orderId, that.orderId) &&
        Objects.equals(transactionId, that.transactionId) && Objects.equals(username, that.username) &&
        Objects.equals(paymentStatus, that.paymentStatus) && Objects.equals(financialOperationType, that.financialOperationType);
  }

  @Override
  public int hashCode() {
    return id.hashCode() * amount.hashCode() * timestamp.hashCode() * orderId.hashCode()
        * transactionId.hashCode() * username.hashCode() * paymentStatus.hashCode()
        * financialOperationType.hashCode();
  }
}
