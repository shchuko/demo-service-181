package com.itmo.microservices.shop.catalog.impl.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class BookingLogRecordStatus {
    @Id
    private Integer id;
    private String name;

    public BookingLogRecordStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public BookingLogRecordStatus() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingLogRecordStatus that = (BookingLogRecordStatus) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "BookingLogRecordStatus{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public enum StatusStrings {
        SUCCESS, FAILED
    }
}
