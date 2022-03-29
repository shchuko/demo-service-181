package com.itmo.microservices.shop.catalog.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.shop.catalog.api.exceptions.BookingNotFoundException
import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException
import com.itmo.microservices.shop.catalog.api.model.BookingDescriptionDto
import com.itmo.microservices.shop.catalog.api.model.BookingLogRecordDTO
import com.itmo.microservices.shop.catalog.api.model.ItemDTO
import com.itmo.microservices.shop.catalog.api.service.ItemService
import com.itmo.microservices.shop.catalog.impl.entity.Booking
import com.itmo.microservices.shop.catalog.impl.entity.BookingLogRecord
import com.itmo.microservices.shop.catalog.impl.entity.BookingLogRecordStatus
import com.itmo.microservices.shop.catalog.impl.entity.BookingStatus
import com.itmo.microservices.shop.catalog.impl.logging.ItemServiceNotableEvents
import com.itmo.microservices.shop.catalog.impl.mapper.mapToBookingLogRecordDTO
import com.itmo.microservices.shop.catalog.impl.mapper.mapToDTO
import com.itmo.microservices.shop.catalog.impl.mapper.mapToEntity
import com.itmo.microservices.shop.catalog.impl.mapper.mapToEntityWithNullId
import com.itmo.microservices.shop.catalog.impl.repository.*
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Suppress("UnstableApiUsage")
@Service
class ItemServiceImpl(
    private val itemRepository: ItemRepository,
    private val bookingRepository: BookingRepository,
    private val bookingStatusRepository: BookingStatusRepository,
    private val bookingLogRecordRepository: BookingLogRecordRepository,
    private val bookingLogRecordStatusRepository: BookingLogRecordStatusRepository,
    private val eventBus: EventBus
) : ItemService {

    @InjectEventLogger
    private lateinit var logger: EventLogger
    val lock = ReentrantLock(true)


    override fun listItems(): MutableList<ItemDTO> =
        itemRepository.findAll().map { it.mapToDTO() }.toMutableList()
            .also { logger.info(ItemServiceNotableEvents.I_GET_ITEMS_REQUEST, it.size) }

    override fun listAvailableItems(): MutableList<ItemDTO> =
        itemRepository.findAllByAmountGreaterThan(0).map { it.mapToDTO() }.toMutableList()
            .also { logger.info(ItemServiceNotableEvents.I_GET_AVAILABLE_ITEMS_REQUEST, it.size) }

    override fun listUnavailableItems(): MutableList<ItemDTO> =
        itemRepository.findAllByAmountLessThanEqual(0).map { it.mapToDTO() }.toMutableList()
            .also { logger.info(ItemServiceNotableEvents.I_GET_UNAVAILABLE_ITEMS_REQUEST, it.size) }

    @Throws(ItemNotFoundException::class)
    override fun getItemCount(id: UUID): Int = itemRepository.findById(id).let { maybeItem ->
        if (maybeItem.isEmpty) {
            logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, id)
            throw ItemNotFoundException("No value with id='$id' exists")
        }
        maybeItem.get().amount.also { logger.info(ItemServiceNotableEvents.I_GET_ITEM_COUNT_REQUEST, it) }
    }

    @Throws(ItemNotFoundException::class)
    override fun deleteItem(id: UUID) {
        try {
            itemRepository.deleteById(id)
            logger.info(ItemServiceNotableEvents.I_DELETE_ITEM_REQUEST, id)
        } catch (e: Throwable) {
            logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, id)
            throw ItemNotFoundException("Cannot delete item with id='$id'", e)
        }
    }

    @Throws(IllegalArgumentException::class)
    override fun createItem(item: ItemDTO) {
        validateItem(item, "createItem: ")
        item.id = null
        itemRepository.save(item.mapToEntityWithNullId())
        logger.info(ItemServiceNotableEvents.I_CREATE_ITEM_REQUEST, item)
    }

    @Throws(IllegalArgumentException::class)
    override fun updateItem(item: ItemDTO) {
        validateItem(item, "updateItem: ")
        val maybeItemEntity = itemRepository.findById(item.id)
        if (maybeItemEntity.isEmpty) {
            logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, item.id)
            throw ItemNotFoundException("Item with id='${item.id}' not found")
        }

        val itemEntity = maybeItemEntity.get()
        BeanUtils.copyProperties(item.mapToEntity(), itemEntity)
        itemRepository.save(itemEntity)
        logger.info(ItemServiceNotableEvents.I_UPDATE_ITEM_REQUEST, itemEntity)
    }

    override fun listBookingLogRecords(bookingId: UUID): MutableList<BookingLogRecordDTO> =
        getBookingOrThrow(bookingId).bookingLogRecords.map { it.mapToBookingLogRecordDTO() }.toMutableList()

    @Throws(IllegalArgumentException::class)
    override fun describeItem(id: UUID): ItemDTO = itemRepository.findById(id).let { maybeItem ->
        if (maybeItem.isEmpty) {
            logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, id)
            throw ItemNotFoundException("No value with $id exists")
        }
        maybeItem.get().mapToDTO()
    }

    @Throws(IllegalArgumentException::class)
    override fun book(items: MutableMap<UUID, Int>): BookingDescriptionDto {
        require(items.all { it.value >= 0 }) { "Negative amount is not allowed" }
        val bookingStatusCreated =
            bookingStatusRepository.getBookingStatusByName(BookingStatus.StatusStrings.CREATED.name)
        val bookingLogRecordStatusSuccess =
            bookingLogRecordStatusRepository.getBookingLogRecordStatusByName(BookingLogRecordStatus.StatusStrings.SUCCESS.name)
        val bookingLogRecordStatusFail =
            bookingLogRecordStatusRepository.getBookingLogRecordStatusByName(BookingLogRecordStatus.StatusStrings.FAILED.name)

        /* Create new booking */
        val booking = bookingRepository.save(Booking(bookingStatusCreated))

        /* bookingResult contains triples of <ItemID, BookedAmount, BookingSuccessFlag> */
        val bookingResult: MutableList<Triple<UUID, Int, Boolean>> = mutableListOf()


        try {
            for (it in items) {
                val itemId = it.key
                val amount = it.value
                val bookingSuccessfulFlag = tryBookItem(itemId, amount)
                bookingResult.add(Triple(itemId, amount, bookingSuccessfulFlag))
            }
        } catch (e: ItemNotFoundException) {
            bookingResult.filter { it.third }.forEach { refundItem(it.first, it.second) }
            throw e
        }
        val successItems = bookingResult.filter { it.third }.associate { it.first to it.second }
        val failedItems = bookingResult.filter { !it.third }.associate { it.first to it.second }

        /* Save booking log record entities */
        for (it in bookingResult) {
            val status = if (it.third) bookingLogRecordStatusSuccess else bookingLogRecordStatusFail
            val timestamp = currentTimeMillis()
            bookingLogRecordRepository.save(BookingLogRecord(booking.id, it.first, status, it.second, timestamp))
        }
        return BookingDescriptionDto(booking.id, successItems, failedItems)
    }


    override fun describeBooking(bookingId: UUID): BookingDescriptionDto {
        val booking = getBookingOrThrow(bookingId);
        val records = booking.bookingLogRecords
            .map { Triple(it.itemId, it.amount, it.bookingLogRecordStatus.toEnum()) }
            .toList()

        val successItems = records.filter { it.third == BookingLogRecordStatus.StatusStrings.SUCCESS }
            .associate { it.first to it.second }
        val failedItems = records.filter { it.third == BookingLogRecordStatus.StatusStrings.FAILED }
            .associate { it.first to it.second }

        return BookingDescriptionDto(bookingId, successItems, failedItems)
    }

    @Suppress("DuplicatedCode")
    @Throws(BookingNotFoundException::class)
    override fun cancelBooking(bookingId: UUID) {
        // TODO refactor if proper warehouse processing requested
        val booking = getBookingOrThrow(bookingId)
        if (booking.bookingStatus.toEnum() != BookingStatus.StatusStrings.CREATED) {
            throw IllegalStateException()
        }

        booking.bookingLogRecords.filter { it.bookingLogRecordStatus.toEnum() == BookingLogRecordStatus.StatusStrings.SUCCESS }
            .forEach { refundItem(it.itemId, it.amount) }

        booking.bookingStatus =
            bookingStatusRepository.getBookingStatusByName(BookingStatus.StatusStrings.CANCELLED.name)
        bookingRepository.save(booking)
    }


    @Suppress("DuplicatedCode")
    @Throws(BookingNotFoundException::class)
    override fun processRefund(bookingId: UUID) {
        // TODO refactor if proper warehouse processing requested
        val booking = getBookingOrThrow(bookingId)
        if (booking.bookingStatus.toEnum() != BookingStatus.StatusStrings.COMPLETE) {
            throw IllegalStateException()
        }

        booking.bookingLogRecords.filter { it.bookingLogRecordStatus.toEnum() == BookingLogRecordStatus.StatusStrings.SUCCESS }
            .forEach { refundItem(it.itemId, it.amount) }

        booking.bookingStatus = bookingStatusRepository.getBookingStatusByName(BookingStatus.StatusStrings.REFUND.name)
        bookingRepository.save(booking)
    }

    @Throws(BookingNotFoundException::class)
    override fun completeBooking(bookingId: UUID) {
        val booking = getBookingOrThrow(bookingId)
        if (booking.bookingStatus.toEnum() != BookingStatus.StatusStrings.CREATED) {
            throw IllegalStateException()
        }

        booking.bookingStatus =
            bookingStatusRepository.getBookingStatusByName(BookingStatus.StatusStrings.COMPLETE.name)
        bookingRepository.save(booking)
    }

    /**
     * Validate item model. If invalid, throw  IAE and log ItemServiceNotableEvents.E_MODEL_VALIDATION_FAILED event.
     *
     * @param item Item to validate
     * @throws IllegalArgumentException if model is invalid.
     */
    @Throws(IllegalArgumentException::class)
    private fun validateItem(item: ItemDTO, prefix: String = "") {
        require(item.amount >= 0) {
            logger.error(
                ItemServiceNotableEvents.E_MODEL_VALIDATION_FAILED, "${prefix}amount=${item.amount} < 0"
            )
            "Item amount cannot be negative"
        }
        require(item.price >= 0) {
            logger.error(ItemServiceNotableEvents.E_MODEL_VALIDATION_FAILED, "${prefix}price=${item.price} < 0")
            "Item price cannot be negative"
        }
    }

    private fun tryBookItem(itemId: UUID, amount: Int): Boolean {
        require(amount >= 0) { "Negative amount='$amount' is not allowed" }
        return lock.withLock {
            val maybeItemEntity = itemRepository.findById(itemId)
            if (maybeItemEntity.isEmpty) {
                logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, itemId)
                throw ItemNotFoundException("Item with id='$itemId' not found")
            }

            val itemEntity = maybeItemEntity.get()
            if (itemEntity.amount < amount) {
                return@withLock false
            }

            itemEntity.amount -= amount
            itemRepository.save(itemEntity)
            return@withLock true
        }
    }

    private fun refundItem(itemId: UUID, amount: Int) {
        require(amount >= 0) { "Negative amount='$amount' is not allowed" }
        return lock.withLock {
            val maybeItemEntity = itemRepository.findById(itemId)
            if (maybeItemEntity.isEmpty) {
                logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, itemId)
                throw ItemNotFoundException("Item with id='$itemId' not found")
            }

            val itemEntity = maybeItemEntity.get()
            itemEntity.amount += amount
            itemRepository.save(itemEntity)
        }
    }

    @Throws(BookingNotFoundException::class)
    private fun getBookingOrThrow(bookingId: UUID): Booking {
        val maybeBooking = bookingRepository.findById(bookingId)
        if (maybeBooking.isEmpty) {
            throw BookingNotFoundException("Booking with id='$bookingId' not found")
        }
        return maybeBooking.get()
    }

    private fun BookingStatus.toEnum() = BookingStatus.StatusStrings.valueOf(name)
    private fun BookingLogRecordStatus.toEnum() = BookingLogRecordStatus.StatusStrings.valueOf(name)
}