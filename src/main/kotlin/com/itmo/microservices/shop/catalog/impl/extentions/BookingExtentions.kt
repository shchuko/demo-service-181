package com.itmo.microservices.shop.catalog.impl.extentions

import com.itmo.microservices.shop.catalog.api.model.BookingLogRecordDTO
import com.itmo.microservices.shop.catalog.impl.entity.BookingLogRecord
import com.itmo.microservices.shop.catalog.impl.entity.BookingStatus
import java.util.*

fun Map.Entry<UUID, Int>.toBookingLogRecord(bookingId: UUID, status: BookingStatus) =
    BookingLogRecord(bookingId, key, value, status)

fun BookingLogRecord.toBookingLogRecordDTO() =
    BookingLogRecordDTO(bookingId, itemId, amount, status, timestamp)
