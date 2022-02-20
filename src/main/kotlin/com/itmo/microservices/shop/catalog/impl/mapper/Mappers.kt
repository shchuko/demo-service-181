package com.itmo.microservices.shop.catalog.impl.mapper

import com.itmo.microservices.shop.catalog.api.model.BookingLogRecordDTO
import com.itmo.microservices.shop.catalog.api.model.ItemDTO
import com.itmo.microservices.shop.catalog.impl.entity.BookingLogRecord
import com.itmo.microservices.shop.catalog.impl.entity.Item

fun ItemDTO.mapToEntity() = Item(id, name, price, description, amount)
fun ItemDTO.mapToEntityWithNullId() = Item(null, name, price, description, amount)

fun Item.mapToDTO() =
    ItemDTO(id, name, description, price, amount)
fun Item.mapToDTOWithNullId() =
    ItemDTO(null, name, description, price, amount)

fun BookingLogRecord.mapToBookingLogRecordDTO() =
    BookingLogRecordDTO(booking.id, itemId, amount, bookingLogRecordStatus.name, timestamp)

