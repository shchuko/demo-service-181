package com.itmo.microservices.shop.catalog.impl.entity;

import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
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
        BookingStatus status = (BookingStatus) o;
        return Objects.equals(id, status.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BookingStatus{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    public BookingStatus() {
    }

    public BookingStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Status list:
     * <ul>
     * <li>CREATED - booking created</li>
     * <li>CANCELLED - booking cancelled by the used</li>
     * <li>COMPLETE - commit booking, it cannot be expired </li>
     * <li>COMPLETE - items refund to catalog by the user </li>
     * </ul>
     * <p>
     * Possible transitions:
     * <ul>
     * <li>CREATED -> CANCELLED</li>
     * <li>CREATED -> COMPLETE</li>
     * <li>COMPLETE -> REFUND</li>
     * </ul>
     */
    public enum StatusStrings {
        CREATED, CANCELLED, REFUND, COMPLETE
    }
}
