package com.itmo.microservices.shop.catalog.impl.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class BookingLogRecordId implements Serializable {
    public UUID bookingId;
    public UUID itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BookingLogRecordId that = (BookingLogRecordId) o;
        return Objects.equals(bookingId, that.bookingId)
                && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, itemId);
    }
}
