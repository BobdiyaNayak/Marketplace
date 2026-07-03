package com.marketplace.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Order {

    public enum Status { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }

    private final String id;
    private final String buyerId;
    private final List<OrderItem> items;
    private final LocalDateTime createdAt;
    private Status status;

    public Order(String id, String buyerId, List<OrderItem> items) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Order id must not be blank");
        if (buyerId == null || buyerId.isBlank()) throw new IllegalArgumentException("Buyer id must not be blank");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Order must contain at least one item");
        this.id = id;
        this.buyerId = buyerId;
        this.items = Collections.unmodifiableList(items);
        this.createdAt = LocalDateTime.now();
        this.status = Status.PENDING;
    }

    public String getId() { return id; }
    public String getBuyerId() { return buyerId; }
    public List<OrderItem> getItems() { return items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public BigDecimal getTotal() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return String.format("Order{id='%s', buyer='%s', total=%s, status=%s, createdAt=%s}",
                id, buyerId, getTotal(), status, createdAt);
    }
}
