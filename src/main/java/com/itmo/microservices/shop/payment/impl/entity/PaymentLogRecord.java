package com.itmo.microservices.shop.payment.impl.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

import static org.hibernate.Hibernate.isInitialized;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentLogRecord {

    @Id
    @GeneratedValue
    private UUID id;
    private Integer amount;
    private Long timestamp;
    private UUID orderId;
    private UUID transactionId;
    private UUID userId;


    @ManyToOne(fetch = FetchType.EAGER)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.EAGER)
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
