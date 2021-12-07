package com.itmo.microservices.shop.payment.impl.entity;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.*;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class PaymentTransactionsProcessorWriteback {

    @Id
    private UUID id;
    private Integer amount;
    private UUID orderId;
    private UUID userId;
    private String financialOperationTypeName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PaymentTransactionsProcessorWriteback paymentTransactionsProcessorWriteback = (PaymentTransactionsProcessorWriteback) o;
        return Objects.equals(id, paymentTransactionsProcessorWriteback.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
