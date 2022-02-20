package com.itmo.microservices.shop.payment.impl.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

import static org.hibernate.Hibernate.isInitialized;

@Entity
@Getter
@Setter
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

    public PaymentLogRecord() {
    }

    public PaymentLogRecord(Integer amount, Long timestamp, UUID orderId, UUID transactionId, UUID userId, PaymentStatus paymentStatus, FinancialOperationType financialOperationType) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.paymentStatus = paymentStatus;
        this.financialOperationType = financialOperationType;
    }

    public PaymentLogRecord(UUID id, Integer amount, Long timestamp, UUID orderId, UUID transactionId, UUID userId, PaymentStatus paymentStatus, FinancialOperationType financialOperationType) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.paymentStatus = paymentStatus;
        this.financialOperationType = financialOperationType;
    }

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
