package com.itmo.microservices.shop.common.transactions;

import java.util.List;
import java.util.Map;

public interface WriteBackStorage<ID, C> {
    void add(ID id, C context);

    void remove(ID id);

    List<Map.Entry<ID, C>> getAll();
}
