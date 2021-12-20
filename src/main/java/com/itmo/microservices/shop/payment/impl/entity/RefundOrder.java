package com.itmo.microservices.shop.payment.impl.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class RefundOrder {

    @Id
    @GeneratedValue
    private UUID id;
    @NotNull
    private UUID orderId;
    @NotNull
    private Double price;
    @NotNull
    private Date requestTime;
    private UUID transactionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        RefundOrder refundOrder = (RefundOrder) o;
        return Objects.equals(id, refundOrder.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
