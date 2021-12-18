package com.itmo.microservices.shop.catalog.impl.service

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.shop.catalog.api.exceptions.ItemNotFoundException
import com.itmo.microservices.shop.catalog.api.model.BookingLogRecordDTO
import com.itmo.microservices.shop.catalog.api.model.ItemDTO
import com.itmo.microservices.shop.catalog.api.service.ItemService
import com.itmo.microservices.shop.catalog.impl.extentions.toBookingLogRecord
import com.itmo.microservices.shop.catalog.impl.extentions.toBookingLogRecordDTO
import com.itmo.microservices.shop.catalog.impl.logging.ItemServiceNotableEvents
import com.itmo.microservices.shop.catalog.impl.mapper.ItemDTOToItemMapper
import com.itmo.microservices.shop.catalog.impl.mapper.ItemToItemDTOMapper
import com.itmo.microservices.shop.catalog.impl.repository.BookingRepository
import com.itmo.microservices.shop.catalog.impl.repository.BookingStatusRepository
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository
import com.itmo.microservices.shop.order.api.model.BookingDTO
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Collectors
import kotlin.concurrent.withLock

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val bookingStatusRepository: BookingStatusRepository,
    private val bookingRepository: BookingRepository
) :
    ItemService {

    @InjectEventLogger
    private lateinit var logger: EventLogger
    val lock = ReentrantLock(true)

    //region orderInjectionMethods

    @Throws(IllegalArgumentException::class)
    override fun getByUuid(uuid: UUID): ItemDTO = itemRepository.findById(uuid).let {
        if (it.isEmpty) {
            logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, uuid)
            throw ItemNotFoundException("No value with $uuid exists")
        }
        ItemToItemDTOMapper.map(it.get())
    }

    override fun bookItems(bookMap: Map<UUID, Int>?): BookingDTO {
        val uuidBookedList = ArrayList<UUID>()
        val bookingId = UUID.randomUUID()
        bookMap?.forEach { item ->
            val isBooked = increaseItemAmount(-1 * item.value, item.key)
            if (!isBooked) {
                uuidBookedList.add(item.key)
                bookingRepository.save(
                    item.toBookingLogRecord(
                        bookingId,
                        bookingStatusRepository.findByName(BookingStatusRepository.VALUES.FAILED.name)
                    )
                )
            } else {
                bookingRepository.save(
                    item.toBookingLogRecord(
                        bookingId,
                        bookingStatusRepository.findByName(BookingStatusRepository.VALUES.SUCCESS.name)
                    )
                )
            }
        }
        return BookingDTO(bookingId, HashSet(uuidBookedList))
    }

    override fun deleteBooking(bookingId: UUID): MutableList<UUID> {
        val uuidBookedList = ArrayList<UUID>()
        getBookingById(bookingId).forEach { bookingItem ->
            val isBooked = increaseItemAmount(bookingItem.amount, bookingItem.itemId)
            if (!isBooked) {
                uuidBookedList.add(bookingItem.itemId)
            }
        }
        return uuidBookedList
    }

    override fun getBookingById(bookingId: UUID): MutableList<BookingLogRecordDTO> =
        bookingRepository.findByBookingId(bookingId)
            .stream()
            .map { it.toBookingLogRecordDTO() }
            .collect(Collectors.toList())

    //endregion

    //region controllerUsedMethods

    override fun getItems(): MutableList<ItemDTO> = itemRepository.findAll()
        .stream()
        .map(ItemToItemDTOMapper::map)
        .collect(Collectors.toList())
        .also {
            logger.info(ItemServiceNotableEvents.I_GET_ITEMS_REQUEST, it.size)
        }

    override fun getAvailableItems(): MutableList<ItemDTO> =
        itemRepository.findAllByAmountGreaterThan(0)
            .stream()
            .map(ItemToItemDTOMapper::map)
            .collect(Collectors.toList())
            .also {
                logger.info(ItemServiceNotableEvents.I_GET_AVAILABLE_ITEMS_REQUEST, it.size)
            }

    override fun getUnavailableItems(): MutableList<ItemDTO> =
        itemRepository.findAllByAmountLessThanEqual(0)
            .stream()
            .map(ItemToItemDTOMapper::map)
            .collect(Collectors.toList())
            .also {
                logger.info(ItemServiceNotableEvents.I_GET_UNAVAILABLE_ITEMS_REQUEST, it.size)
            }

    @Throws(ItemNotFoundException::class)
    override fun getCountOfItem(uuid: UUID): Int = itemRepository.findById(uuid).let {
        if (it.isEmpty) {
            logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, uuid)
            throw ItemNotFoundException("No value with $uuid exists")
        }
        it.get().amount.also { amount ->
            logger.info(ItemServiceNotableEvents.I_GET_ITEM_COUNT_REQUEST, amount)
        }
    }

    @Throws(ItemNotFoundException::class)
    override fun deleteItem(uuid: UUID) {
        itemRepository.findById(uuid).let {
            if (it.isEmpty) {
                logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, uuid)
                throw ItemNotFoundException("No value with $uuid exists")
            }
            with(it.get()) {
                itemRepository.delete(this)
                logger.info(ItemServiceNotableEvents.I_DELETE_ITEM_REQUEST, this)
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    override fun createItem(itemDTO: ItemDTO) {
        if (itemDTO.amount < 0 || itemDTO.price < 0) {
            logger.error(
                ItemServiceNotableEvents.E_MODEL_VALIDATION_FAILED,
                "amount=${itemDTO.amount}, price=${itemDTO.price}"
            )
            throw IllegalArgumentException("Wrong parameters for creating item")
        }
        itemRepository.save(ItemDTOToItemMapper.map(itemDTO))
        logger.info(ItemServiceNotableEvents.I_CREATE_ITEM_REQUEST, itemDTO)
    }

    @Throws(ItemNotFoundException::class, IllegalArgumentException::class)
    override fun updateItem(uuid: UUID, itemDTO: ItemDTO) {
        itemRepository.findById(uuid).apply {
            if (isEmpty) {
                logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, uuid)
                throw ItemNotFoundException("No value with $uuid exists")
            } else if (itemDTO.amount < 0 || itemDTO.price < 0) {
                logger.error(
                    ItemServiceNotableEvents.E_MODEL_VALIDATION_FAILED,
                    "'amount' and 'price' must be more than 0"
                )
                throw IllegalArgumentException("Wrong parameters for updating item")
            }
            BeanUtils.copyProperties(itemDTO, get(), "id")
            logger.info(ItemServiceNotableEvents.I_UPDATE_ITEM_REQUEST, get())
            itemRepository.save(get())
        }
    }

    //endregion

    @Throws(IllegalArgumentException::class)
    fun increaseItemAmount(diff: Int, itemUuid: UUID): Boolean {
        lock.withLock {
            itemRepository.findById(itemUuid).apply {
                return when {
                    isEmpty -> {
                        logger.error(ItemServiceNotableEvents.E_ITEM_WITH_UUID_NOT_FOUND, itemUuid)
                        throw ItemNotFoundException("No value with $itemUuid exists")
                    }
                    (get().amount + diff < 0) -> {
                        false
                    }
                    else -> {
                        get().amount += diff
                        logger.info(ItemServiceNotableEvents.I_UPDATE_ITEM_REQUEST, get())
                        itemRepository.save(get())
                        true
                    }
                }
            }
        }
    }
}