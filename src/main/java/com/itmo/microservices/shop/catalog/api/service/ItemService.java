package com.itmo.microservices.shop.catalog.api.service;

import com.itmo.microservices.shop.catalog.api.model.BookingDescriptionDto;
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
    @NotNull
    List<ItemDTO> listItems();

    /**
     * List available items of catalog.
     *
     * @return List of available items.
     */
    @NotNull
    List<ItemDTO> listAvailableItems();

    /**
     * List unavailable items of catalog;
     *
     * @return List of unavailable items.
     */
    @NotNull
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
    @NotNull
    List<BookingLogRecordDTO> listBookingLogRecords(@NotNull UUID bookingId);

    //endregion

    //region injectionOrderService

    /**
     * Describe item.
     *
     * @param id Item ID.
     * @return Item description.
     */
    @NotNull
    ItemDTO describeItem(@NotNull UUID id);

    /**
     * Create new booking. Initial status of booking is "CREATED".
     *
     * @param items Map of items to book. The key is the item ID, the value is requested item count.
     * @return Booking description.
     */
    @NotNull
    BookingDescriptionDto book(@NotNull Map<UUID, Integer> items);

    /**
     * Describe existing booking.
     *
     * @param bookingId Booking ID.
     */
    @NotNull
    BookingDescriptionDto describeBooking(@NotNull UUID bookingId);

    /**
     * Cancel created booking. Can be called only if booking status is "COMPLETE".
     *
     * @param bookingId Booking ID.
     */
    void cancelBooking(@NotNull UUID bookingId);

    /**
     * Unload items from catalog aka transfer to delivery service.
     * Can be called only if booking status is "COMPLETE".
     *
     * @param bookingId Booking ID.
     */
    void processRefund(@NotNull UUID bookingId);

    /**
     * Complete the booking. Allowed only if booking status is "CREATED".
     * Status changes of "COMPLETE" booking is not allowed, it cannot be refund or cancelled.
     *
     * @param bookingId Booking ID.
     */
    void completeBooking(@NotNull UUID bookingId);

    //endregion
}
