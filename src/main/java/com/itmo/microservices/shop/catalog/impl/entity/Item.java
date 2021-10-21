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
  private UUID uuid;
  private String name;
  private Integer price;
  private String description;
  private String count;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Item item = (Item) o;
    return uuid != null && Objects.equals(uuid, item.uuid) && Objects.equals(name, item.name)
        && Objects.equals(price, item.price) && Objects.equals(description, item.description)
        && Objects.equals(count, item.count);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode() * name.hashCode() * price.hashCode() * description.hashCode()
        * count.hashCode();
  }
}
