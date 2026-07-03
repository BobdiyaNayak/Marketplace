package com.marketplace.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Cart {

    private final String buyerId;
    private final Map<String, Integer> items; // productId -> quantity

    public Cart(String buyerId) {
        if (buyerId == null || buyerId.isBlank()) throw new IllegalArgumentException("Buyer id must not be blank");
        this.buyerId = buyerId;
        this.items = new HashMap<>();
    }

    public String getBuyerId() { return buyerId; }

    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public void addItem(String productId, int quantity) {
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product id must not be blank");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        items.merge(productId, quantity, Integer::sum);
    }

    public void removeItem(String productId) {
        items.remove(productId);
    }

    public void updateQuantity(String productId, int quantity) {
        if (quantity <= 0) {
            removeItem(productId);
        } else {
            items.put(productId, quantity);
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal calculateTotal(java.util.function.Function<String, BigDecimal> priceResolver) {
        return items.entrySet().stream()
                .map(e -> priceResolver.apply(e.getKey()).multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return String.format("Cart{buyerId='%s', items=%s}", buyerId, items);
    }
}
