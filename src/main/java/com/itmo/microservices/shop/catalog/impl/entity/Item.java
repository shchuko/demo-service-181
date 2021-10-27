package com.itmo.microservices.shop.catalog.impl.entity;


import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
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
public class Item {

  @Id
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
}
