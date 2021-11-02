package com.itmo.microservices.shop.catalog.impl.entity;


import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private Integer price;
    private String description;
    private Integer amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(id, item.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Integer getAmount() {
        return amount;
    }
}
