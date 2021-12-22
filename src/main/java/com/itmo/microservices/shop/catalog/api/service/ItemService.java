package com.itmo.microservices.shop.catalog.api.service;

import com.itmo.microservices.shop.catalog.api.model.BookingLogRecordDTO;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import com.itmo.microservices.shop.order.api.model.BookingDTO;
import kotlin.Pair;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ItemService {

    //region injectionOrderService

    ItemDTO getByUuid(UUID uuid);

    BookingDTO bookItems(Map<UUID, Integer> bookMap);

    List<UUID> deleteBooking(UUID bookingId);

    List<BookingLogRecordDTO> getBookingById(UUID bookingId);
    //endregion

    //region controllerUsedMethods

    List<ItemDTO> getItems();

    List<ItemDTO> getAvailableItems();

    List<ItemDTO> getUnavailableItems();

    int getCountOfItem(UUID uuid);

    void deleteItem(UUID uuid);

    void createItem(ItemDTO item);

    void updateItem(UUID uuid, ItemDTO itemDTO);
    //endregion


}
