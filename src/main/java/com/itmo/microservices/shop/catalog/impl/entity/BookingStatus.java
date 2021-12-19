package com.itmo.microservices.shop.catalog.impl.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class BookingStatus {

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
        BookingStatus paymentStatus = (BookingStatus) o;
        return Objects.equals(id, paymentStatus.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
