package com.itmo.microservices.shop.user.impl.entity;

import java.util.Objects;
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
public class User {

  @Id
  private String username;
  private String passwordHash;
  private Boolean isAdmin;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    User user = (User) o;
    return username != null && Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
