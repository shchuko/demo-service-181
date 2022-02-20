package com.itmo.microservices.shop.catalog.api.service;

import com.itmo.microservices.shop.catalog.api.model.BookingCreationDto;
import com.itmo.microservices.shop.catalog.api.model.BookingLogRecordDTO;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ItemService {
    //region controllerUsedMethods

    /**
     * List all items in catalog.
     *
     * @return List of items in catalog.
     */
    List<ItemDTO> listItems();

    /**
     * List available items of catalog.
     *
     * @return List of available items.
     */
    List<ItemDTO> listAvailableItems();

    /**
     * List unavailable items of catalog;
     *
     * @return List of unavailable items.
     */
    List<ItemDTO> listUnavailableItems();

    /**
     * Get item count.
     *
     * @param id Item ID.
     * @return Count of item.
     */
    int getItemCount(@NotNull UUID id);

    /**
     * Delete item from catalog.
     *
     * @param id Item ID.
     */
    void deleteItem(@NotNull UUID id);

    /**
     * Create new item in catalog.
     *
     * @param item Item description.
     */
    void createItem(@NotNull ItemDTO item);

    /**
     * Update item in catalog.
     *
     * @param item New item description.
     */
    void updateItem(@NotNull ItemDTO item);

    /**
     * Describe booking log records.
     *
     * @param bookingId Booking id to describe.
     * @return List of booking
     */
    List<BookingLogRecordDTO> listBookingLogRecords(@NotNull UUID bookingId);

    //endregion

    //region injectionOrderService

    /**
     * Describe item.
     *
     * @param id Item ID.
     * @return Item description.
     */
    ItemDTO describeItem(@NotNull UUID id);

    /**
     * Create new booking. Initial status of booking is "CREATED".
     * The booking will expire after bookingLiveTimeMillis timeot and will become "CANCELLED".
     *
     * @param items Map of items to book. The key is the item ID, the value is requested item count.
     * @return Booking description.
     */
    BookingCreationDto book(@NotNull Map<UUID, Integer> items, long bookingLiveTimeMillis);

    /**
     * Cancel created booking. Can be called only if booking status is "CREATED".
     *
     * @param bookingId Booking ID.
     */
    void cancelBooking(@NotNull UUID bookingId);

    /**
     * Commit the booking. Allowed only if booking status is "CREATED".
     * Booking "COMMITTED" means it can never expire and become "CANCELLED".
     *
     * @param bookingId Booking ID.
     */
    void commitBooking(@NotNull UUID bookingId);

    /**
     * Unload items from catalog aka transfer to delivery service.
     * Can be called only if booking status is "COMMITTED".
     *
     * @param bookingId Booking ID.
     */
    void processRefund(@NotNull UUID bookingId);

    /**
     * Complete the booking. Allowed only if booking status is "COMMITTED".
     * Status changes of "COMPLETE" booking is not allowed, it cannot be refund or cancelled.
     *
     * @param bookingId Booking ID.
     */
    void completeBooking(@NotNull UUID bookingId);

    //endregion
}
