package com.itmo.microservices.shop.payment.impl.entity;

import static org.hibernate.Hibernate.isInitialized;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class PaymentLogRecord {

  @Id
  private UUID id;
  private Integer amount;
  private Long timestamp;
  private UUID orderId;
  private UUID transactionId;
  private UUID userId;

  @ManyToOne(fetch = FetchType.LAZY)
  private PaymentStatus paymentStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  private FinancialOperationType financialOperationType;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    PaymentLogRecord paymentLogRecord = (PaymentLogRecord) o;
    return Objects.equals(id, paymentLogRecord.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    /* Using vanilla Java instead of lombok because of LAZY fetch type */
    return "PaymentLogRecord(" +
        "id=" + id +
        ", amount=" + amount +
        ", timestamp=" + timestamp +
        ", orderId=" + orderId +
        ", transactionId=" + transactionId +
        ", userId=" + userId +
        ", paymentStatus=" +
        (isInitialized(paymentStatus) ? paymentStatus : "<NOT_FETCHED>") +
        ", financialOperationType=" +
        (isInitialized(financialOperationType) ? financialOperationType : "<NOT_FETCHED>") + ")";
  }
}
