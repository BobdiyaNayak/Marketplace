package com.marketplace.repository;

import com.marketplace.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductRepository {

    private final Map<String, Product> store = new LinkedHashMap<>();

    public void save(Product product) {
        store.put(product.getId(), product);
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Product> findBySellerId(String sellerId) {
        return store.values().stream()
                .filter(p -> sellerId.equals(p.getSellerId()))
                .collect(Collectors.toList());
    }

    public boolean delete(String id) {
        return store.remove(id) != null;
    }

    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}
