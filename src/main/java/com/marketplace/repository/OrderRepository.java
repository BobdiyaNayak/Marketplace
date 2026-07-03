package com.marketplace.repository;

import com.marketplace.model.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderRepository {

    private final Map<String, Order> store = new LinkedHashMap<>();

    public void save(Order order) {
        store.put(order.getId(), order);
    }

    public Optional<Order> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Order> findByBuyerId(String buyerId) {
        return store.values().stream()
                .filter(o -> buyerId.equals(o.getBuyerId()))
                .collect(Collectors.toList());
    }

    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}
