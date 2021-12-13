package com.itmo.microservices.shop.delivery.impl.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class DeliveryTransactionsProcessorWriteback {
    @Id
    private UUID id;
    private UUID orderId;
    private UUID userId;
    private int timeSlot;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryTransactionsProcessorWriteback that = (DeliveryTransactionsProcessorWriteback) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
