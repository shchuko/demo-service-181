package com.itmo.microservices.shop.catalog.impl.logging;

import com.itmo.microservices.commonlib.logging.NotableEvent;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public enum ItemServiceNotableEvents implements NotableEvent {
    I_GET_ITEMS_REQUEST ("Requested all items, found {}"),
    I_GET_AVAILABLE_ITEMS_REQUEST ("Requested available items, found {}"),
    I_GET_UNAVAILABLE_ITEMS_REQUEST ("Requested unavailable items, found {}"),
    I_GET_ITEM_COUNT_REQUEST("Requested get item's count, found {}"),
    I_UPDATE_ITEM_REQUEST("Requested update item. Updated item: {}"),
    I_CREATE_ITEM_REQUEST("Requested create item. Created item: {}"),
    I_DELETE_ITEM_REQUEST("Requested delete item. Deleted item UUID: {}"),

    E_ITEM_WITH_UUID_NOT_FOUND("Item not found. Item UUID: {}"),
    E_MODEL_VALIDATION_FAILED("Model ItemDTO validation failed, expected amount>0 and price>0. {}"),
    E_CONSISTENCY_ANOMALY("Consistency anomaly. Anomaly: {}");

    private final String template;

    @NotNull
    @Override
    public String getName() {
        return this.name();
    }

    @NotNull
    @Override
    public String getTemplate() {
        return this.template;
    }
}
